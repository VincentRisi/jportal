echo "Installing jportal dependencies..."
sudo apt-get -y update
sudo apt-get install -y cmake
sudo apt-get install -y eclipse
sudo apt-get install -y javacc
sudo apt-get install -y cocor
sudo apt-get install -y log4j
sudo apt-get install -y postgresql 

#Install some eclipse plugins
#cmakeed
#eclipse -nosplash -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/releases/indigo/,http://cmakeed.sourceforge.net/eclipse/ -installIU com.cthing.cmakeed.feature.feature.group

#pydev, first install the required cert, then install pydev
sudo ./install_pydev_certificate.sh
sudo eclipse -nosplash -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/releases/indigo/,http://pydev.org/updates/ -installIU org.python.pydev.feature.feature.group


#Oracle
#Alien is required to install the RPM packages
sudo apt-get install -y --force-yes alien
#libAIO is required by oracle
sudo apt-get install -y libaio-dev
#sudo apt-get install -y libaio1
 
#Alien can't install the oracle files if they reside on a vagrant shared folder, so copy them to ~/oracle_temp and install from there...
mkdir ~/oracle_temp
cp oracle/* ~/oracle_temp/
cd ~/oracle_temp
sudo alien -i oracle-instantclient12.1-basiclite-12.1.0.2.0-1.x86_64.rpm
sudo alien -i oracle-instantclient12.1-jdbc-12.1.0.2.0-1.x86_64.rpm 
sudo alien -i sudo alien -i oracle-instantclient12.1-devel-12.1.0.2.0-1.x86_64.rpm
#sudo alien -i oracle-instantclinet*-sqlplus-*.rpm

#set oracle confs
sudo sh -c "echo /usr/lib/oracle/12.1/client/lib >/etc/ld.so.conf.d/oracle.conf"
sudo ldconfig

#TODO: Must be added to .bashrc??
export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib:$LD_LIBRARY_PATH
