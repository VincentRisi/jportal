#ifndef _HTTPREQUEST_H_
#define _HTTPREQUEST_H_

#include "machine.h"
#include "logfile.h"
#include "tbuffer.h"
#include "popgen.h"
#include "cbsocket.h"
#include "xstring.h"
#include "percent_encoding.h"
#include "httpxcept.h"

class HTTPRequest
{
    tCBSockServer &sockServer;
    tLogFile      &logFile;
public:
    char* operation;
    char* path;
    char* content_type;
    char* accept;
    char* host;
    char* user_agent;
    char* authorization;
    char* expect;
    char* connection;
    char* origin;
    char* accept_encoding;
    char* accept_language;
    int   content_length;
    bool  https;
    string input;
    string output;
    HTTPRequest(tCBSockServer &aSockServer, tLogFile &aLogFile)
        : sockServer(aSockServer)
        , accept(0)
        , accept_encoding(0)
        , accept_language(0)
        , authorization(0)
        , connection(0)
        , content_length(0)
        , content_type(0)
        , expect(0)
        , path(0)
        , host(0)
        , logFile(aLogFile)
        , origin(0)
        , user_agent(0)
        , operation(0)
    {
    #if defined(USE_OPENSSL)
        if (sockServer.fSockCB->ssl != 0)
            https = true;
        else
    #endif
            https = false;
        logFile.Log("HTTPRequest started", eLogDebug);
    }
    ~HTTPRequest()
    {
        if (accept) free(accept);
        if (accept_encoding) free(accept_encoding);
        if (accept_language) free(accept_language);
        if (authorization) free(authorization);
        if (connection) free(connection);
        if (content_type) free(content_type);
        if (expect) free(expect);
        if (path) free(path);
        if (host) free(host);
        if (origin) free(origin);
        if (user_agent) free(user_agent);
        if (operation) free(operation);
        logFile.Log("HTTPRequest ended", eLogDebug);
    }
    void readData(uint32 &read, TBChar &buffer);
    string readRequest();
    void writeResponse(string output);
    void writeError204(const char* error);
    void writeError400(const char* error, const char* content_type="text/plain");
    const char* loadHeaders(TBChar &buffer, uint32 size);
    const char* getAuthorizedData(TBChar &buffer);
};

#endif