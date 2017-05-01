package jportal

import (
    "database/sql"
)

type GoRet struct {
  head, output, sequence, tail string
  useBind bool
}

func (ret *GoRet) checkUse(code string) string {
  if ret.useBind == true {
    return code
  }
  return ""
}
