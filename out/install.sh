mkdir loadsLog
mkdir scheduleLogs
touch apis.properties
echo 'pre="/Allocator/index.jsp?json="
VMON={"id":"powerOn","vmName":""}
VMOFF={"id":"powerOff","vmName":""}
DOCKERON={"id":"createContainer","dockerName":""}
DOCKERFF={"id":"stop","dockerName":""}' >> apis.properties
echo "CREATO apis.properties."
touch config.properties
echo '#Db config
user=
password=
db=test
procs_table=mach_test
host_table=QRTZ_VMWARE_HOST
schedule_table=SCHEDULE
#log config
loads_path="/loadsLog"
general_file="/logs"' >> config.properties
echo "CREATO config.properties."

touch allocator.properties
echo 'hostname="hostname"
port=8080' >> allocator.properties
echo "CREATO allocator.properties."
