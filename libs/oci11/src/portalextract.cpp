#include "machine.h"
#include "ti.h"
#include "dbportal.h"
#include "portalextract.h"
#include "sqlbin.h"
#include "sqlcons.h"

#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <math.h>
#include <errno.h>

/*------------------------------------------------------------------------------
   xPortalExtract Constructor
------------------------------------------------------------------------------*/
xPortalExtract::xPortalExtract(const char *file, const int line, const char *fmt, ...)
               :xCept((char *)file, (int) line, "xPortalExtract", -1)
{
   va_list ap;

   char jotter[4096];

   va_start(ap, fmt);

   vsnprintf(jotter, sizeof(jotter) - 1, fmt, ap);

   va_end(ap);

   osErr << jotter << endl << ends;
}


/*------------------------------------------------------------------------------
   PortalExtract Constructor
------------------------------------------------------------------------------*/
PortalExtract::PortalExtract(tDBConnect &dbConnect) : m_DBConnect(dbConnect)
{
}


/*------------------------------------------------------------------------------
   PortalExtract Destructor
------------------------------------------------------------------------------*/
PortalExtract::~PortalExtract()
{
}


/*------------------------------------------------------------------------------
   builds the fields from portal struct using Table as a reference
------------------------------------------------------------------------------*/
void PortalExtract::Extract(const void           *portalStruct,
                            const char           *SqlQuery,
                            AppendList<MapField> *Fields)
{
   MapField          mapField;
   tSqlQuery        *sqlQuery;
   unsigned short    index;

   if (SqlBinQuery(m_DBConnect.CB()->SqlBin, &sqlQuery, (char *) SqlQuery))
      throw xPortalExtract(__FILE__, __LINE__, "Error loading query '%s' - SqlBin Error '%s'", SqlQuery, m_DBConnect.CB()->SqlBin->ErrorMsg);

   Fields->Reset();

   for (index = 0; index < sqlQuery->NoFields; index++)
   {
      memset(&mapField, 0, sizeof(mapField));

      switch (sqlQuery->Fields[index].CType)
      {
         case SQL_C_CHAR      :
            strncpy(mapField.Value, (char*)((char*)portalStruct + sqlQuery->Fields[index].Offset), sizeof(mapField.Value)-1);
            break;

         case SQL_C_DOUBLE    :
            mapField.doubleValue = *(double*)((char*)portalStruct + sqlQuery->Fields[index].Offset);
            sprintf(mapField.Value, "%.2lf", mapField.doubleValue);
            break;

         case SQL_C_LONG      :
            mapField.longValue = *(long*)((char*)portalStruct + sqlQuery->Fields[index].Offset);
            sprintf(mapField.Value, "%ld", mapField.longValue);
            break;

         case SQL_C_SHORT:
            mapField.shortValue = *(short*)((char*)portalStruct + sqlQuery->Fields[index].Offset);
            sprintf(mapField.Value, "%d", mapField.shortValue);
            break;

         default              :
            throw xPortalExtract(__FILE__, __LINE__, "Cannot Map Field Type '%d/%s' - SqlBin Query '%s'", sqlQuery->Fields[index].CType, sqlQuery->Fields[index].Name, SqlQuery);
      }

      strncpy(mapField.Name, sqlQuery->Fields[index].Name + 1, sizeof(mapField.Name)-1);

      Fields->Append(&mapField);
   }
}


/*------------------------------------------------------------------------------
   fills in the portal struct from the fields
------------------------------------------------------------------------------*/
void PortalExtract::Fill(void                       *portalStruct,
                         const char                 *SqlQuery,
                         const AppendList<MapField> *Fields)
{
   tSqlQuery        *sqlQuery;
   unsigned short    index;
   unsigned          field_index;
   MapField         *map;

   if (SqlBinQuery(m_DBConnect.CB()->SqlBin, &sqlQuery, (char *) SqlQuery))
      throw xPortalExtract(__FILE__, __LINE__, "Error loading query '%s' - SqlBin Error '%s'", SqlQuery, m_DBConnect.CB()->SqlBin->ErrorMsg);

   for (field_index = 0; field_index < Fields->Count(); field_index++)
   {
      map = Fields->Get(field_index);

      for (index = 0; index < sqlQuery->NoFields; index++)
      {
         if (stricmp(map->Name, sqlQuery->Fields[index].Name + 1))
            continue;

         switch (sqlQuery->Fields[index].CType)
         {
            case SQL_C_CHAR      :
               strncpy((char*)((char*)portalStruct + sqlQuery->Fields[index].Offset), map->Value, sqlQuery->Fields[index].Precision);
               break;

            case SQL_C_DOUBLE    :
               *(double*)((char*)portalStruct + sqlQuery->Fields[index].Offset) = map->doubleValue;
               break;

            case SQL_C_LONG      :
               *(long*)((char*)portalStruct + sqlQuery->Fields[index].Offset) = map->longValue;
               break;

            case SQL_C_SHORT     :
               *(short*)((char*)portalStruct + sqlQuery->Fields[index].Offset) = map->shortValue;
               break;

            default              :
               throw xPortalExtract(__FILE__, __LINE__, "Cannot Map Field Type '%d/%s' - SqlBin Query '%s'", sqlQuery->Fields[index].CType, sqlQuery->Fields[index].Name, SqlQuery);
         }

         break;
      }
   }
}

/*------------------------------------------------------------------------------
   copy one portal structure to another
------------------------------------------------------------------------------*/
void PortalExtract::Convert(const void  *fromPortalStruct, 
                            const char  *fromSqlQuery,
                            void        *toPortalStruct,
                            const char  *toSqlQuery)
{
   AppendList<MapField> fields;
   Extract(fromPortalStruct, fromSqlQuery, &fields);
   Fill(toPortalStruct, toSqlQuery, &fields);
}
