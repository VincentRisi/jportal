#include "jsontest.h"
#include "json_databuild.h"
#include "lookup.inl"

const char* in_data =
"{\n"
"  \"bankaccproc\": {\n"
"    \"functions\": \"BankAccountFromLT\",\n"
"    \"BankAccountFromLT\": {\n"
"      \"listed\": false,\n"
"      \"input\": {\n"
"        \"fields\": \"SwiftAddress CurrId AccountTypeId\",\n"
"        \"SwiftAddress\": {\n"
"          \"usetype\": \"string\",\n"
"          \"length\": 12,\n"
"          \"precision\": 11,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Char\"\n"
"        },\n"
"        \"CurrId\": {\n"
"          \"usetype\": \"string\",\n"
"          \"length\": 5,\n"
"          \"precision\": 4,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Char\"\n"
"        },\n"
"        \"AccountTypeId\": {\n"
"          \"usetype\": \"string\",\n"
"          \"length\": 17,\n"
"          \"precision\": 16,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Char\"\n"
"        }\n"
"      },\n"
"      \"output\": {\n"
"        \"fields\": \"Rc OciErr ErrBuff AccountNo\",\n"
"        \"Rc\": {\n"
"          \"usetype\": \"int\",\n"
"          \"length\": 4,\n"
"          \"precision\": 9,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Int\"\n"
"        },\n"
"        \"OciErr\": {\n"
"          \"usetype\": \"int\",\n"
"          \"length\": 4,\n"
"          \"precision\": 9,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Int\"\n"
"        },\n"
"        \"ErrBuff\": {\n"
"          \"usetype\": \"string\",\n"
"          \"length\": 4096,\n"
"          \"precision\": 4095,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Char\"\n"
"        },\n"
"        \"AccountNo\": {\n"
"          \"usetype\": \"string\",\n"
"          \"length\": 65,\n"
"          \"precision\": 64,\n"
"          \"scale\": 0,\n"
"          \"datatype\": \"Char\"\n"
"        }\n"
"      }\n"
"    }\n"
"  }\n"
"}\n";

void decode(const char* data)
{
    Value root;
#ifdef USE_CHAR_READER
    const char* enddata = data+strlen(data);
    string errors;
    CharReaderBuilder builder;
    CharReader * reader = builder.newCharReader();
    bool parsed = reader->parse(data, enddata, &root, &errors);
    delete reader;
#else
    stringstream sstr(data);
    sstr >> root;
#endif
    string name = root.begin().name();
    Value table = root[name];
    Value functions_list = table["functions"];
    const char* functions = functions_list.asCString();
}

void encode()
{
    Value root;
    Value record;
    int count = 20;
    root["_count_"] = count;
    const char* record_name = "silly_putty";
    root["_name_"] = record_name;
    int8 i8 = 100;
    const char*name = "i8";
    record[name] = i8;
    int16 i16 = 30000;
    name = "i16";
    record[name] = i16;
    int32 i32 = 3000000;
    name = "i32";
    record[name] = i32;
    int64 i64 = 30000000000000000;
    name = "i64";
    record[name] = i64;
    name = "MaryDuck";
    const char* value = "Mary had a little duck, she wished she had a little luck";
    size_t len = strlen(value)+1;
    string namesize = string(name) + "_size_";
    record[namesize.c_str()] = len;
    record[name] = value;
    root[record_name] = record;
    FastWriter writer;
    string fred = writer.write(record);
    cout << fred << endl;
}

void play_with_lookup()
{
    tLookup rec;
    memset(&rec, 0, sizeof(rec));
    strncpy(rec.Name, "WAZZABERRY", sizeof(rec.Name) - 1);
    strncpy(rec.Refs, "FRUIT_LOOPS", sizeof(rec.Refs) - 1);
    strncpy(rec.Value, "Pounds and pounds of worth", sizeof(rec.Value) - 1);
    strncpy(rec.USId, "JSONTEST", sizeof(rec.USId) - 1);
    strncpy(rec.TmStamp, "20181130080101", sizeof(rec.TmStamp) - 1);
    DataBuilderJson builder;
    rec.BuildData(builder);
    string result;
    builder.getRecord(result);
    cout << result << endl;
    builder.clear();
    builder.setRecord(result.c_str());
    rec.SetData(builder);
}

#include "streams.inl"
void play_with_streams()
{
  string input = 
      "{ \"Signature\": 1045109256,"
      "  \"Rec\":"
      "  {\"Id\": 123456,"
      "    \"MessageType\": 0,"
      "    \"MessageLen\": 0,"
      "    \"MessageData\": \"\""
      "  }"
      "}";
  int32 _Result;
  Value input_value;
  stringstream sstr(input);
  sstr >> input_value;
  DataBuilderJson builder;
  //input
  tStreamsByMessageID Rec;
  Value Rec_value = input_value["Rec"];
  builder.setValue(Rec_value);
  Rec.SetData(builder);

  //output
  int32 NoOf;
  struct auto1
  {
      tStreamsByMessageID* arr;
      auto1() { arr = 0; }
      ~auto1() { if (arr != 0) free(arr); }
  } Recs;
  for (int i = 0; i < NoOf; i++)
  {
      tStreamsByMessageID &rec = Recs.arr[i];
  }
}

int main(int argc, char* argv[])
{
    //decode(in_data);
    //encode();
    play_with_lookup();
    //Brain b;
    //int x = b.get_a();
    play_with_streams();
}

