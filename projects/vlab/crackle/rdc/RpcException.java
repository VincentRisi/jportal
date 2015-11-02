package vlab.crackle.rdc;

public class RpcException extends Throwable
{
  private static final long serialVersionUID = 1L;
  public RpcException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
