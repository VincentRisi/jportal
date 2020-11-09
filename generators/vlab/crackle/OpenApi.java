package vlab.crackle;

import java.io.Serializable;
import java.util.Vector;

/**
* "'POST'GET'PUT'DELETE'OPTIONS'HEAD'PATCH'TRACE'"
*/
public class OpenApi implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final byte
      POST       =  0
    , PUT        =  1
    , PATCH      =  2
    , GET        =  3
    , DELETE     =  4
    , OPTIONS    =  5
    , HEAD       =  6
    , TRACE      =  7
    , IN_ERROR   = -1
    ;
  public String  path;
  public String  tags;
  public byte    typeof;
  public OpenApi()
  {
    path = "";
    tags = "";
    typeof = POST;
  }
  public String getType()
  {
    switch(typeof)
    {
    case OpenApi.GET:
      return "get";
    case OpenApi.PUT:
      return "put";
    case OpenApi.PATCH:
      return "patch";
    case OpenApi.DELETE:
      return "delete";
    case OpenApi.OPTIONS:
      return "options";
    case OpenApi.HEAD:
      return "head";
    case OpenApi.TRACE:
      return "head";
    default:
      return "post";
    }
  }
  public boolean isPathed()
  {
    if (typeof == OpenApi.POST || typeof == OpenApi.PUT || typeof == OpenApi.PATCH)
      return false;
    if (path.contains("/{")) 
      return true;
    return false;
  }
}
