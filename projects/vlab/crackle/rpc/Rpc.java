/*
 * Created on Jan 7, 2004
 */
package vlab.crackle.rpc;

/**
 * @author vince
 */
public interface Rpc
{
	Object call(int message, int signature, Object object) throws Throwable;
	Object call(int message, int signature) throws Throwable;
	Rpc open();
	public Object read();
	void write(Object object);
	void close();
  public void setUserId(String userId);
  public String getUserId();
  public void setLocationId(String locationId);
  public String getLocationId();
}
