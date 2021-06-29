// This uses a modified version of jsoncpp in order to work on AIX 13.1.3
// Copyright 2011 Baptiste Lepilleur and The JsonCpp Authors
// Distributed under MIT license, or public domain if desired and
// recognized in your jurisdiction.
// See file LICENSE for detail or copy at http://jsoncpp.sourceforge.net/LICENSE

#include "json_databuild.h"

DataBuilderJson::DataBuilderJson()
{
}

DataBuilderJson::~DataBuilderJson()
{
}

void DataBuilderJson::setRecord(const char* data)
{
    clear();
    stringstream sstr(data);
    sstr >> record;
}

void DataBuilderJson::setValue(Value inValue)
{
    clear();
    record = inValue;
}

Value DataBuilderJson::getRecord(string &data)
{
    StreamWriterBuilder swb;
    data = writeString(swb, record);
    return record;
}

void DataBuilderJson::count(int value)
{
    _count = value;
}

void DataBuilderJson::name(const char *value)
{
    _name = value;
}

void DataBuilderJson::add(const char *name, int8 value, const char *io)
{
    record[name] = value;
}

void DataBuilderJson::add(const char *name, int16 value, const char *io)
{
    record[name] = value;
}

void DataBuilderJson::add(const char *name, int32 value, const char *io)
{
    record[name] = value;
}

void DataBuilderJson::add(const char *name, int64 value, const char *io)
{
    record[name] = value;
}

void DataBuilderJson::add(const char *name, char *value, size_t size, const char *io)
{
#if defined(JSON_GENERATE_SIZE)
    string namesize = string(name) + "_size_";
    record[namesize.c_str()] = (int)size;
#endif
    record[name] = value;
}

void DataBuilderJson::add(const char *name, double value, const char *io)
{
    record[name] = value;
}

void DataBuilderJson::add(const char *name, void *value, size_t size, const char *io)
{
    int no;
    const char* bp = (char*)value;
    for (no = size; no > 0; no--)
        if (bp[no - 1] != 0x00)
            break;
    if (no > 4)
        record[name] = base64_encode((unsigned char const*)value + 4, (unsigned int)no - 4);
    else
        record[name] = "";
}

void DataBuilderJson::add(const char *name, tHugeCHAR &value, const char *io)
{
    record[name] = value.data;
}

void DataBuilderJson::set(const char *name, int8   &value, size_t size, const char *io)
{
    int64 work = record[name].asInt64();
    if (work > -127 && work < 128)
        value = (int8)work;
    else
    {
        snprintf(sn_buff, sizeof(sn_buff), "Value %s exceeds range for int8 %d.", name, (int)work);
        throwRuntimeError(sn_buff);
    }
}

void DataBuilderJson::set(const char *name, int16  &value, size_t size, const char *io)
{
    int64 work = record[name].asInt64();
    if (work >= -32768 && work <= 32768)
        value = (int16)work;
    else
    {
        snprintf(sn_buff, sizeof(sn_buff), "Value %s exceeds range for int16 %d.", name, (int)work);
        throwRuntimeError(sn_buff);
    }
}

void DataBuilderJson::set(const char *name, int32  &value, size_t size, const char *io)
{
    int64 work = record[name].asInt64();
    if (work >= -2147483647 && work <= 2147483648)
        value = (int32)work;
    else
    {
        snprintf(sn_buff, sizeof(sn_buff), "Value %s exceeds range for int32 %d.", name, (int)work);
        throwRuntimeError(sn_buff);
    }
}

void DataBuilderJson::set(const char *name, int64  &value, size_t size, const char *io)
{
    value = record[name].asInt64();
}

void DataBuilderJson::set(const char *name, char *value, size_t size, const char *io)
{
    string work = record[name].asString();
    size_t from_size = work.length();
    if (from_size < size)
        strncpyz(value, work.c_str(), from_size);
    else
    {
        snprintf(sn_buff, sizeof(sn_buff), "Value %s sizeof string %d exceeds allowed size %d.", name, from_size, size);
        throwRuntimeError(sn_buff);
    }
}

void DataBuilderJson::set(const char *name, double &value, size_t size, const char *io)
{
    value = record[name].asDouble();
}

void DataBuilderJson::set(const char *name, void *value, size_t size, const char *io)
{
    string from4 = record[name].asString();
    string as3 = base64_decode(from4);
    size_t as3_size = as3.length();
    if (as3_size + 4 <= size)
    {
        memset(value, 0, size);
        *(int*)value = as3_size;
        memcpy((char*)value+4, (void *)as3.c_str(), as3_size);
    }
    else
    {
        snprintf(sn_buff, sizeof(sn_buff), "Value %s sizeof string %d exceeds allowed size %d.", name, as3_size, size);
        throwRuntimeError(sn_buff);
    }
}

void DataBuilderJson::set(const char *name, tHugeCHAR &value, size_t size, const char *io)
{
    string work = record[name].asString();
    size_t from_size = work.length();
    if (from_size < size)
    {
        if (value.data != 0)
            delete[] value.data;
        value.data = new char[from_size + 1];
        strncpyz(value.data, work.c_str(), from_size);
        value.length = from_size;
    }
    else
    {
        snprintf(sn_buff, sizeof(sn_buff), "Value %s sizeof string %d exceeds allowed size %d.", name, from_size, size);
        throwRuntimeError(sn_buff);
    }
}

