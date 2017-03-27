package bbd.binu.parser;

public class TJBlob
{
  private int used;
  private int maxSize;
  private byte[] buffer;
  public TJBlob(int maxSize)
  {
    this.maxSize = 8388608;
    buffer = new byte[4];
    buffer[0] = buffer[1] = buffer[2] = buffer[3] = 0;
    used = 4;
  }
  public void setBytes(byte[] data)
  {
    int size = data.length;
    used = size + 4;
    if (used > maxSize)
    {
      size = maxSize - 4;
      used = maxSize;
    }
    buffer = new byte[used];
    for (int i = 0; i < size; i++)
      buffer[i + 4] = data[i];
    setSize(size);
  }
  private void setSize(int size)
  {
    buffer[0] = (byte) ((size & 0xFF000000) >>> 24);
    buffer[1] = (byte) ((size & 0x00FF0000) >> 16);
    buffer[2] = (byte) ((size & 0x0000FF00) >> 8);
    buffer[3] = (byte) (size & 0x000000FF);
  }
  public int length()
  {
    return (int)((buffer[0]& 0x000000FF) << 24) 
         + (int)((buffer[1]& 0x000000FF) << 16) 
         + (int)((buffer[2]& 0x000000FF) << 8) 
         + (int)(buffer[3]& 0x000000FF);
  }
  public byte[] getBytes()
  {
    int size = length();
    byte[] result = new byte[size];
    for (int i = 0; i < size; i++)
      result[i] = buffer[i + 4];
    return result;
  }
}
