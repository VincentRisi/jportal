#include "httpxcept.h"

static const char* HTTPErrors[] =
{ "OK ???"
, "Unhandled Message Type"
, "Only use ascii character data"
, "Only content type of json allowed"
, "Wait on read failed"
, "Zero data read assume closed"
, "POST input request not correct"
, "Unhandled ... exception"
};

xHTTPError::xHTTPError(const char* aFname, int aLine, eError aError, int aNo, const char* aMsg)
: xCept(aFname, aLine, "xHTTPError", aError) 
{
    osErr << "Socket " << HTTPErrors[aError] << ":" << aNo << endl;
    if (aMsg)
        osErr << "1) " << aMsg << endl;
    osErr << ends;
}
