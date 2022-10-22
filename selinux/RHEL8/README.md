SELinux policies for Db2 LUW

1. Make sure selinux is enabled in /etc/sysconfig/selinux:

	# SELINUX= can take one of these three values:
	#	enforcing - SELinux security policy is enforced.
	#	permissive - SELinux prints warnings instead of enforcing.
	#	disabled - SELinux is fully disabled.
	SELINUX=enforcing    <---- set to "enforcing"

   If SELinux was not previously enforcing, then reboot.

2. As root run ./db2.sh

This will compile and install the db2 policy and relabel Db2 install files.

3. Run the "ibm_db2_semanage_db2inst" script to apply SELinux labels to the
   files and directories in the instance owner's home directory:

 # ./ibm_db2_semanage_db2inst -a db2inst1 /home/db2inst1
 # ls -Z /home/db2inst1/sqllib
drwxr-sr-x. db2inst1 db2inst1 unconfined_u:object_r:db2_file_t:s0 acs
drwxr-sr-x. db2inst1 db2inst1 unconfined_u:object_r:db2_exec_t:s0 adm
lrwxrwxrwx. root     db2inst1 unconfined_u:object_r:db2_file_t:s0 adsm -> /opt/ibm/db2/V11.1/adsm
drwxr-s---. db2inst1 db2inst1 unconfined_u:object_r:db2_file_t:s0 backup
lrwxrwxrwx. root     db2inst1 unconfined_u:object_r:db2_exec_t:s0 bin -> /opt/ibm/db2/V11.1/bin
lrwxrwxrwx. root     db2inst1 unconfined_u:object_r:db2_file_t:s0 bnd -> /opt/ibm/db2/V11.1/bnd
drwxrwsr-t. db2inst1 db2inst1 unconfined_u:object_r:db2_file_t:s0 cfg
...


4. Reboot the machine to restart the DB2 Fault Monitor

5. The DB2 instance's processes will also run in their own domain(s):
	$ ps aux -Z | grep db2inst1

unconfined_u:unconfined_r:db2d_t:s0-s0:c0.c1023 root 4057 0.1  4.6 1333568 179500 pts/0 Sl 12:29   0:00 db2wdog 0 [db2inst1]
unconfined_u:unconfined_r:db2d_t:s0-s0:c0.c1023 db2inst1 4128 0.8  5.5 1662196 213248 pts/0 Sl 12:29   0:00 db2sysc 0
unconfined_u:unconfined_r:db2d_t:s0-s0:c0.c1023 db2inst1 4270 0.1  1.2 637780 47144 pts/0 S 12:30   0:00 db2vend (PD Vendor Process - 1) 0
system_u:system_r:initrc_t:s0   root      4339  0.0  0.0 115404  1448 ?        S    12:30   0:00 sh -c /opt/ibm/db2/V11.1/bin/db2fm -i db2inst1 -m /opt/ibm/db2/V11.1/lib64/libdb2gcf.so.1 -S 2>&1
system_u:system_r:initrc_t:s0   db2inst1  4340  0.1  0.3 183836 14492 ?        S    12:30   0:00 /opt/ibm/db2/V11.1/bin/db2fm -i db2inst1 -m /opt/ibm/db2/V11.1/lib64/libdb2gcf.so.1 -S
unconfined_u:unconfined_r:db2d_t:s0-s0:c0.c1023 db2inst1 4344 0.2  0.3 179248 13976 pts/0 S 12:30   0:00 db2fmpr  ,0,0,0,1,0,0,00000000,0,0,0,0000000000000000,0000000000000000,00000000,00000000,00000000,00000000,00000000,00000000,0000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000,00000000bc4c4000,0000000000000000,0000000000000000,1,0,0,,,,,a80164,14,1e014,2,0,1,0000000000041fc0,0x240000000,0x240000000,1600000,10013,2,18016


Troubleshooting

A. Watch /var/log/messages for output like this:
	setroubleshoot:      SELinux is preventing /opt/ibm/db2/V9.5/bin/db2fm (db2_t) "use" to /dev/null (init_t).      For complete SELinux messages. run sealert -l 0be517de-b797-4aec-b274-8b936d77cf95

B. If any are found:
     i) Run the sealert command with the parameters that were given in /var/log/messages:
	sealert -l 0be517de-b797-4aec-b274-8b936d77cf95

    ii) Cut and paste the Raw Audit Message into a file, and pass the file to "audit2allow"

   iii) Add necessary the permission to db2.te, rebuild db2.pp, and reload it
        (steps 2 and 3, above)

3. The command "semanage fcontext -l" will list file contexts. Use with grep
   to find contexts related to DB2.

