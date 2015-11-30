import os, os.path, struct, binu

def main(inp, args):
    data_dir = inp.data_dir
    delim    = inp.delim
    logfile  = inp.logfile
    proc_id  = inp.proc_id
    connect  = inp.connect
    ifilename = '%s%sproc_%d.qry' % (data_dir, delim, int(proc_id))
    if os.path.exists(ifilename) == False:
        print 'file %s does not exist - returning -1' % (ifilename)
        return -1
    binu.__init__(connect, logfile)
    ifilename = ifilename.replace('\\', '/')
    ifile = open(ifilename, 'rb')
    buffer = ifile.read()
    ifile.close()
    os.unlink(ifilename)
    ofs = 0
    query_len = struct.unpack_from('!l', buffer, ofs)[0];ofs+=4
    query     = struct.unpack_from('%ds' % (query_len), buffer, ofs)[0];ofs+=int(query_len)
    rec_len   = struct.unpack_from('!l', buffer, ofs)[0];ofs+=4
    rec       = struct.unpack_from('%ds' % (rec_len), buffer, ofs)[0]
    no_recs, out_buffer_size, out_buffer = binu.Multiple(query_len, query, rec_len, rec, 0, 0, '')
    ofilename = '%s\\proc_%d.data' % (data_dir, int(proc_id))
    ofilename = ofilename.replace('\\', '/')
    ofile = open(ofilename, 'wb')
    ofile.write(struct.pack('!l', int(no_recs)))
    ofile.write(struct.pack('!l', int(out_buffer_size)))
    if out_buffer_size > 0:
        ofile.write(struct.pack('%ds' % (out_buffer_size), out_buffer))
    ofile.close()
    return 0
    