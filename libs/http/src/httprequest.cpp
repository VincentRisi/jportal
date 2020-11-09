#include "httprequest.h"
#include "httpserver.h"

static const char* _getline(char *line, int size, const char* bp)
{
    line[0] = 0;
    int n = strcspn(bp, "\r\n");
    if (n > size)
        return 0;
    if (n != 0)
    {
        strncpy(line, bp, n);
        line[n] = 0;
        bp += n;
    }
    if (*bp == '\r') bp += 2;
    else if (*bp == '\n') bp += 1;
    return bp;
}

inline int begins(char* line, char* value)
{
    if (strnicmp(line, value, strlen(value)) == 0)
        return strlen(value);
    return 0;
}

static const char* Operations = "'POST'GET'PUT'DELETE'OPTIONS'HEAD'PATCH'TRACE'";

const char* HTTPRequest::loadHeaders(TBChar &buffer, uint32 size)
{
    TBChar work;
    const char *bp = buffer.data;
    const char *np = strchr(bp, ' ');
    work.set(bp, np - bp);
    operation = strdup(work.data);
    bp = np + 1;
    np = strchr(bp, ' ');
    int bp_size = np - bp;
    if (work.size < bp_size)
        work.resize(bp_size + 32);
    percent_decode((char*)bp, np - bp, work.data, work.size);
    path = strdup(work.data);
    bp = np + 1;
    char line[512];
    snprintf(line, 512, "'%s'", operation);
    if (strstr(Operations, line) == 0)
    {
        logFile.lprintf(eLogError, "Not a supported REST CODE: '%20.20s'", operation);
        throw XHTTP_ERROR(ERR_HTTP_UNHANDLED, "not a supported REST CODE");
    }
    if (HTTPServer::restful == false)
    {
        if (strcmp(operation, "POST") != 0)
        {
            logFile.lprintf(eLogError, "Not POST '%20.20s'", operation);
            throw XHTTP_ERROR(ERR_HTTP_NOT_POST, "not POST");
        }
    }
    for (;;)
    {
        int n;
        bp = _getline(line, sizeof(line), bp);
        if (line[0] == 0)
            break;
        n = begins(line, "Content-Type: ");
        if (n > 0)
        {
            content_type = strdup(&line[n]);
            //1234567890123456
            if (strncmp(content_type, "application/json", 16) != 0)
            {
                logFile.lprintf(eLogError, "Content-Type: %s -- invalid here", content_type);
                throw XHTTP_ERROR(ERR_HTTP_NOT_JSON, line);
            }
            continue;
        }
        n = begins(line, "Content-Length: ");
        if (n > 0)
        {
            content_length = atoi(&line[n]);
            continue;
        }
        n = begins(line, "Host: ");
        if (n > 0)
        {
            host = strdup(&line[n]);
            continue;
        }
        n = begins(line, "User-Agent: ");
        if (n > 0)
        {
            user_agent = strdup(&line[n]);
            logFile.lprintf(eLogDebug, "User-Agent: %s", user_agent);
            continue;
        }
        n = begins(line, "Accept: ");
        if (n > 0)
        {
            accept = strdup(&line[n]);
            continue;
        }
        n = begins(line, "Authorization: ");
        if (n > 0)
        {
            authorization = strdup(&line[n]);
            continue;
        }
        n = begins(line, "Expect: ");
        if (n > 0)
        {
            expect = strdup(&line[n]);
            continue;
        }
        n = begins(line, "Connection: ");
        if (n > 0)
        {
            connection = strdup(&line[n]);
            continue;
        }
        n = begins(line, "Origin: ");
        if (n > 0)
        {
            origin = strdup(&line[n]);
            continue;
        }
        n = begins(line, "Accept-Encoding: ");
        if (n > 0)
        {
            accept_encoding = strdup(&line[n]);
            continue;
        }
        n = begins(line, "Header Accept-Language: ");
        if (n > 0)
        {
            accept_language = strdup(&line[n]);
            continue;
        }
        logFile.lprintf(eLogDebug, "%s in header not dealt with here", line);
    }
    if (content_length > 0 && expect == 0)
    {
        while (strlen(bp) < content_length)
        {
            TBChar added;
            added.clear();
            bool toRead = sockServer.WaitRead(100);
            uint32 read = 0;
            readData(read, added);
            if (read == 0)
                logFile.lprintf(eLogDebug, "Length of data after headers %d expects %d.", read, content_length);
            else
                buffer.append(added.data);
        }
    }
    return bp;
}

const char* output_100 = 
    "HTTP/1.1 100 %s\r\n"
    "\r\n";

void HTTPRequest::readData(uint32 &read, TBChar &buffer)
{
    const int size = 8192;
    char work[size];
    while (true)
    {
        uint32 now = sockServer.ReadStream((void*)work, size);
        if (now == 0)
            break;
        read += now;
        buffer.append(work, now);
        if (sockServer.WaitRead(10) == false)
            break;
    }
}

const char* HTTPRequest::getAuthorizedData(TBChar &buffer)
{
    buffer.clear();
    uint fsize = (uint)snprintf(buffer.data, buffer.size, output_100, expect);
    sockServer.Write(buffer.data, fsize);
    bool toRead = sockServer.WaitRead(10000);
    if (toRead == false)
        throw XHTTP_ERROR(ERR_HTTP_READWAIT_FAILED, "after 10000 millisecs");
    buffer.clear();
    uint32 read = 0;
    readData(read, buffer);
    return buffer.data;
}

string HTTPRequest::readRequest()
{
    TBChar buffer(256 * 1024);
    bool toRead = sockServer.WaitRead(10000);
    if (toRead == false)
        throw XHTTP_ERROR(ERR_HTTP_READWAIT_FAILED, "after 10000 millisecs");
    uint32 read = 0;
    readData(read, buffer);
    if (buffer.used == 0)
        throw XHTTP_ERROR(ERR_HTTP_ZERODATA_READ, "no Data to process");
    const char* data = loadHeaders(buffer, read);
    if (authorization != 0 && expect != 0)
    {
        logFile.lprintf(eLogDebug, "Authorization: %s", authorization);
        logFile.lprintf(eLogDebug, "Expect: %s", expect);
        data = getAuthorizedData(buffer);
    }
    string input = data;
    return input;
}

const char* output_200 =
    "HTTP/1.1 200 OK\r\n"
    "Content-Type: application/json\r\n"
    "Content-Length: %d\r\n"
    "Connection: Closed\r\n"
    "\r\n%s";

const char* output_204 = 
    "HTTP/1.1 204 No Content\r\n"
    "Content-Type: text/plain\r\n"
    "Content-Length: %d\r\n"
    "\r\n"
    "%s";

void HTTPRequest::writeError204(const char* error)
{
    uint fsize;
    TBChar buffer(1024);
    fsize = snprintf(buffer.data, buffer.size, output_204, strlen(error), error);
    sockServer.Write(buffer.data, fsize);
}

const char* error_str = "{\"error\":\"%s\"}";

void HTTPRequest::writeResponse(string output)
{
    uint size;
    uint fsize;
    TBChar buffer(1024);
    if (output.find("ERR400:") == 0)
    {
        snprintf(buffer.data, buffer.size, error_str, output.substr(7).c_str());
        writeError400(buffer.data, "application/json");
        return;
    }
    if (output.length() > 0)
    {
        size = strlen(output_200) + 256 + output.length();
        buffer.sizeto(size);
        fsize = snprintf(buffer.data, buffer.size, output_200, output.length(), output.c_str());
    }
    else
    {
        buffer.set(output_204);
        fsize = buffer.used;
    }
    sockServer.Write(buffer.data, fsize);
}

const char* output_400 =
    "HTTP/1.1 400 Bad Request\r\n"
    "Content-Type: %s\r\n"
    "Content-Length: %d\r\n"
    "\r\n"
    "%s";

void HTTPRequest::writeError400(const char* error, const char* content_type)
{
    uint fsize;
    TBChar buffer(1024);
    fsize = snprintf(buffer.data, buffer.size, output_400, content_type, strlen(error), error);
    sockServer.Write(buffer.data, fsize);
}
