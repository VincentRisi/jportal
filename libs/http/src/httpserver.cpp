#include "httpserver.h"

bool HTTPServer::restful = false;
bool HTTPServer::shutdown = false;
#if defined(M_AIX)
static struct sigaction Action;
extern "C" void ShutdownHandler(int arg)
{
    HTTPServer::shutdown = true;
}
static void shutdown_signal(int sig)
{
    Action.sa_handler = ShutdownHandler;
    sigaction(sig, &Action, 0);
}
#elif defined(M_GNU)
static struct sigaction Action;
extern "C" void ShutdownHandler(int arg)
{
    HTTPServer::shutdown = true;
}
static void shutdown_signal(int sig)
{
    signal(sig, ShutdownHandler);
}
#endif

HTTPServer::HTTPServer(char* ConfigFile, char* LogFileName)
{
    this->ConfigFile = strdup(ConfigFile);
    this->LogFileName = strdup(LogFileName);
    this->ServiceStart = 0;
    this->ServiceClient = 0;
    shutdown = false;
#if defined(M_AIX) || defined(M_GNU)
    shutdown_signal(SIGUSR1);
    shutdown_signal(SIGTERM);
    semaphore = 0;
#endif
}

void HTTPServer::OpenSocket()
{
#if defined(USE_OPENSSL)
    if (usessl == true)
        sockServer = new tCBSockServer(service, certfile, cafile, capath, keyfile, keypassword);
    else
#endif    
        sockServer = new tCBSockServer(service);
}

void HTTPServer::LoadConfig()
{
    tString work;
    tINI INI(ConfigFile);
    INI.SetSection("[Server Options]");
    INI.QueryValueDefault("{Log}", work, LogFileName);
    free(LogFileName);
    LogFileName = strdup(work.c_str());
    INI.QueryValueDefault("{LogLevel}", work, "0");
    int no = atoi(work.c_str());
    if (no >= 0 && no <= 3)
        logLevel = (eLevel)no;
    else
        logLevel = eLogInfo;
    INI.QueryValueDefault("{Service}", work, "12345");
    service = strdup(work.c_str());
    INI.QueryValueDefault("{Timeout}", work, "30000");
    timeout = atoi(work.c_str());
    INI.QueryValueDefault("{NoProcs}", work, "1");
    noProcs = atoi(work.c_str());
    INI.QueryValueDefault("{Debug}", work, "1");
    debug = atoi(work.c_str()) != 0;
    INI.QueryValueDefault("{LogReceive}", work, "0");
    logReceive = atoi(work.c_str()) != 0;
    INI.QueryValueDefault("{LogTransmit}", work, "0");
    logTransmit = atoi(work.c_str()) != 0;
    INI.QueryValueDefault("{Metrics}", work, "0");
    metrics = atoi(work.c_str()) != 0;
    INI.QueryValueDefault("{WaitForChildren}", work, "0");
    waitForChildren = atoi(work.c_str()) != 0;
    INI.QueryValueDefault("{RestartCount}", work, "0");
    restartCount = atoi(work.c_str());
#if defined(USE_OPENSSL)
    INI.QueryValueDefault("{UseSSL}", work, "0");
    usessl = atoi(work.c_str()) != 0;
    if (usessl == true)
    {
        INI.QueryValueDefault("{CertFile}", work, "");
        certfile = strdup(work.c_str());
        INI.QueryValueDefault("{CAFile}", work, "");
        cafile = strdup(work.c_str());
        INI.QueryValueDefault("{CAPath}", work, "");
        capath = strdup(work.c_str());
        INI.QueryValueDefault("{KeyFile}", work, "");
        keyfile = strdup(work.c_str());
        INI.QueryValueDefault("{KeyPassword}", work, "");
        keypassword = strdup(work.c_str());
    }
#endif    
    INI.SetSection("[DataBase]");
    INI.QueryValueDefault("{BinFile}", work, "");
    binFileName = strdup(work.c_str());
    INI.QueryValueDefault("{Connection}", work, "");
    connectString = strdup(work.c_str());
}

void HTTPServer::WaitForCall(tLogFile& logFile)
{
    while (shutdown == false)
    {
        while (true)
        {
#if defined(M_AIX) || defined(M_GNU)
            if (shutdown == true)
                return;
            Mutex wait(semaphore);
            if (wait.failed)
            {
                logFile.lprintf(eLogDebug, "Mutex wait failed - errno:%d", errno);
                return;
            }
#endif
            int rc = sockServer->WaitConnect(timeout);
            if (rc == 0)
            {
                if (shutdown == true)
                {
                    logFile.lprintf(eLogInfo, "Process shutdown requested");
                    return;
                }
                continue;
            }
            try
            {
                sockServer->Open();
            }
            catch (xSockErr& x) // ERROR 76 poss requires a semaphore
            {
                if (strstr(x.ErrorStr(), "SockSocket error 76"))
                    continue;
                throw;
            }
            break;
        }
        bool written = false;
        HTTPRequest request(*sockServer, logFile);
        try
        {
            string input = request.readRequest();
            string path = request.path+1;
            if (HTTPServer::restful == true)
            {
                if (input.length() == 0)
                {
                    if (strncmp(request.operation, "GET", 3) == 0 
                    ||  strncmp(request.operation, "HEAD", 4) == 0
                    ||  strncmp(request.operation, "OPTIONS", 7) == 0
                    ||  strncmp(request.operation, "DELETE", 6) == 0)
                    {
                        logFile.Log(path.c_str());
                        Interpret(input, path);
                    }
                }
            }
            if (logReceive == true)
            {
                if (input.length() == 0)
                    logFile.Log("No Input");
                else
                    logFile.lprintf(eLogDebug, "Received:\n%s", input.c_str());
            }
            string function = "";
            if (HTTPServer::restful == true)
            {
                function.append(request.operation);
                function.append(" ");
            }
            function.append(path);
            string output;
            ServiceClient(function.c_str(), input, output);
            if (output.length() > 0 && logTransmit == true)
                logFile.lprintf(eLogDebug, "Transmit:\n%s", output.c_str());
            request.writeResponse(output);
            written = true;
        }
        catch (xCept ex)
        {
            logFile.lprintf(eLogError, "Exception %s caught %d", ex.ErrorStr(), ex.Error());
            logFile.Log(ex);
            request.writeError400(ex.ErrorStr());
        }
        catch (Json::Exception je)
        {
            logFile.lprintf(eLogError, "Json (jsoncpp) Exception Caught: %s", je.what());
            if (written == false)
                request.writeError400(je.what());
        }
        catch (...)
        {
            logFile.Log("Unknown Exception Caught");
            request.writeError400("Unknown Exception Caught");
        }
        sockServer->Close();
        continue;
    }
}

void HTTPServer::Interpret(string& input, string& path)
{
    size_t quest = path.find('?');
    if (quest > 0 && quest < path.length())
    {
        string data = path.substr(quest+1);
        path = path.substr(0, quest);
        input ="{";
        Splitter setters(32);
        setters.read(data.c_str(), '&');
        for (int i = 0; i < setters.no_flds(); i++)
        {
            char field[256 + 4096],key[256],value[4096];
            setters.toch(field, sizeof(field), i);
            Splitter kv(2);
            kv.read(field, '=');
            kv.toch(key, sizeof(key), 0);
            kv.toch(value, sizeof(value), 1);
            snprintf(field, sizeof(field), "\"%s\": %s", key, value);
            if (i > 0) input.append(", ");
            input.append(field);
        }
        input.append("}");
        return;
    }
    Splitter pairs(64);
    pairs.read(path.c_str(), '/');
    int no = pairs.no_flds();
    input = "";
    char field[4096];
    for (int i = no % 2; i < pairs.no_flds(); i += 2)
    {
        if (input.length() > 0) input.append("|");
        pairs.toch(field, sizeof(field), i + 1);
        pairs.asch("?", i + 1);
        input.append(field);
    }
    pairs.make(field,sizeof(field), '/');
    path = field;
    return;
}

#if defined(M_AIX) || defined(M_GNU)

struct PIDList
{
    pid_t* list;
    int noOf;
    PIDList(int noOf)
    {
        this->noOf = noOf;
        list = new pid_t[noOf];
    }
    ~PIDList()
    {
        noOf = 0;
        delete[] list;
    }
};

static int _waitForChildren(PIDList& pidList, int& restartCount, int& noProcs, int& childrenToOpen, tLogFile& logFile, int& rc)
{
    int status;
    struct rusage usage;
    pid_t pid;
    if ((pid = wait3(&status, WNOHANG | WUNTRACED, &usage)) <= 0)
        return 0;
    for (int i = 0; i < pidList.noOf; i++)
    {
        if (pidList.list[i] == pid)
        {
            pidList.list[i] = 0;
            while (i + 1 < pidList.noOf)
            {
                pidList.list[i] = pidList.list[i + 1];
                pidList.list[i + 1] = 0;
                i++;
            }
        }
    }
    rc = (int)WEXITSTATUS(status);
    logFile.lprintf(eLogDebug, "rc:%d = WEXITSTATUS(status)", rc);
    if (rc != 0)
    {
        if (restartCount > 0 && noProcs > 0)
        {
            noProcs--;
            restartCount--;
        }
    }
    else
    {
        if (noProcs > 0)
            noProcs--;
        if (childrenToOpen > 0)
            childrenToOpen--;
    }
    return 1;
}

static void _killChildren(PIDList& pidList)
{
    pid_t pid;
    for (int i = 0; i < pidList.noOf; i++)
    {
        pid = pidList.list[i];
        if (pid != 0)
            kill(pid, SIGUSR1);
    }
}

int HTTPServer::CreateHandlers(tLogFile& logFile)
{
    semaphore = Mutex::makePrivate();
    int  pid = getpid();
    try
    {
        if (debug == true || noProcs <= 1)
        {
            shutdown_signal(SIGQUIT);
            int rc = ServiceStartPid(logFile, pid);
            return rc;
        }
        bool nothingHappened;
        int origRestartCount = restartCount;
        int  childrenToOpen = noProcs;
        PIDList pidList(noProcs);
        noProcs = 0;
        char jotter[128];
        snprintf(jotter, sizeof(jotter) - 1, "/%s%d.%d", (char*)service, noProcs, getpid());
        while (true)
        {
            nothingHappened = true;
            if (noProcs < childrenToOpen)
            {
                if ((pid = fork()) == 0)             // 0 == child
                {
                    pid = getpid();
                    int rc = ServiceStartPid(logFile, pid);
                    logFile.lprintf(eLogDebug, "Child Process (%d) ended pid(%d) rc(%d)", noProcs, pid, rc);
                    return rc;
                }
                else if (pid < 0)
                {
                    nothingHappened = false;
                    childrenToOpen--;
                    logFile.lprintf(eLogError, "Process(%d) failed RC(%d)", noProcs, pid);
                }
                else
                {
                    nothingHappened = false;
                    pidList.list[noProcs] = (int)pid;
                    noProcs++;
                    logFile.lprintf(eLogInfo, "Process(%d) started PID(%d)", noProcs, pid);
                }
                continue;
            }
            if (waitForChildren == 0)
            {
                logFile.lprintf(eLogInfo, "Not required to wait for children");
                return 0;
            }
            if (shutdown == true)
            {
                logFile.lprintf(eLogInfo, "Required to kill child processes");
                _killChildren(pidList);
                return 0;
            }
            int rc;
            if (_waitForChildren(pidList, restartCount, noProcs, childrenToOpen, logFile, rc) != 0)
                nothingHappened = false;
            if (childrenToOpen == 0)
            {
                logFile.lprintf(eLogInfo, "No need to handle children amymore - terminating parent");
                delete sockServer;
                pid = getpid();
                logFile.lprintf(eLogDebug, "Parent Process (%d) ended %d", pid, rc);
                return 0;
            }
            if (nothingHappened == true)
            {
                usleep(10000);
                restartCount = origRestartCount;
            }
        }
    }
    catch (xCept& x)
    {
        logFile.lprintf(eLogError, "Exception caught in httpserver::CreateHandlers - PID = %ld", pid);
        logFile.Log(x);
        throw;
    }
    catch (Json::Exception je)
    {
        logFile.lprintf(eLogError, "Json Exception caught in httpserver::CreateHandlers - PID = %ld", pid);
        throw;
    }
    catch (...)
    {
        logFile.lprintf(eLogError, "Unknown Exception caught in httpserver::CreateHandlers - PID = %ld", pid);
        throw XHTTP_ERROR(ERR_HTTP_ELLIPSIS_UNHANDLED, "Unknown Exception caught in httpserver::CreateHandlers");
    }
}
#else
int HTTPServer::CreateHandlers(tLogFile& logFile)
{
    if (logLevel == eLogDebug)
        logFile.SetDisplay(true);
    return ServiceStart(logFile);
}
#endif

HTTPServer::~HTTPServer()
{
    if (sockServer->IsOpen())
        sockServer->Close();
}
