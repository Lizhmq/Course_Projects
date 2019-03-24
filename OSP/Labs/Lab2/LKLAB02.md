## LKLAB02 -- Lzzz

#### 1-2 GitHub Update

![a](C:\Users\Joe\Documents\Docs\Grade_3\OSP\Lab2\a.PNG)



#### 3 Init_debug & trace

* sudo vim /etc/default/grub 修改GRUB_CMDLINE_LINUX

  ![b](C:\Users\Joe\Documents\Docs\Grade_3\OSP\Lab2\b.PNG)

* reboot

* dmesg > res.txt

  ![e](C:\Users\Joe\Documents\Docs\Grade_3\OSP\Lab2\e.PNG)



#### 4 Generate SVG with bootgraph.pl

* cat res.txt | perl xxxx/xxx.pl > res.svg

  ![c](C:\Users\Joe\Documents\Docs\Grade_3\OSP\Lab2\c.PNG)



#### 5 Initcall file / level / function

* ```shell
  dmesg -s 128000 | grep "initcall" | sed "s/\(.*\)after\(.*\)/\2 \1/g" | sort -rn
  ```

  ```
   1970169 usecs [   10.530005] initcall intel8x0_driver_init+0x0/0x1000 [snd_intel8x0] returned 0 
   1056051 usecs [    1.303827] initcall populate_rootfs+0x0/0x10f returned 0 
   561259 usecs [    4.901766] initcall init_module+0x0/0x1000 [raid6_pq] returned 0 
   535734 usecs [    2.276042] initcall ahci_pci_driver_init+0x0/0x1000 [ahci] returned 0 
   520251 usecs [    2.251529] initcall e1000_init_module+0x0/0x1000 [e1000] returned 0 
   502997 usecs [    2.239085] initcall piix4_driver_init+0x0/0x1000 [i2c_piix4] returned 0 
   488326 usecs [    2.221422] initcall pacpi_pci_driver_init+0x0/0x1000 [pata_acpi] returned 0 
   144540 usecs [    8.086482] initcall vbox_init+0x0/0x1000 [vboxvideo] returned 0 
   131590 usecs [    1.926119] initcall aesni_init+0x0/0x20e [aesni_intel] returned 0 
   124279 usecs [    0.168663] initcall acpi_init+0x0/0x35e returned 0 
  ```

  Intel8x0

  path:	sound/pci/intel8x0.c

  level:	6: device_initcall

  function:	ALSA driver for Intel ICH (i8x0) chipsets

  ​			Called by module_pci_driver(), which is the helper macro for registering a PCI driver

* Populate_rootfs

  path:	init/initramfs.c

  level:	rootfs

  function:	load the built in initramfs

  ​			load default modules from initramfs

* Init_module -- raid6_pq

  path:	lib/raid6/

  level:	

  function:	init the raid6

* Ahci_pci_driver_init

  path:	drivers/ata/ahci.c

  level:	6: device_initcall

  function:	init ahci pci device

* E1000_init_module

  path:	drivers/net/ehernet/intel/e1000e/netdev.c

  level:	6: device_initcall

  function:	Driver Registration Routine

  ​			the first routine called when the driver is loaded

  ​			register with the PCI subsystem

* Piix4_driver_init

  path:	drivers/i2c/busses/i2c-piix4.c

  level:	6: device_initcall

  function:	init Intel PIIX4 device

  ​			piix4 is multi-function PCI device implementing a PCI-to-ISA bridge, a PCI IDE function

  ​			, a USB host/hub funcition and an EPM function

* Pacpi_pci_driver

  path:	drivers/ata/pata_acpi.c

  level:	6: device_initcall

  function:	init ACPI PATA driver

  ​			ACPI provides a set of BIOS methods that allow an SFF compliant controller to be

  ​			 configured and managed by the OS without specific OS support for the controller

* Vbox_init

  path:	drivers/staging/vboxvideo/vbox_drv.c

  level:	6: device_initcall

  function:	init vbox_pci_driver

* Aesni_init

  path:	arch/x86/crypto/aesni-intel_glue.c

  level:	7: late_initcall

  function:	support for Intel AES-NI instructions

* Acpi_init

  path:	drivers/acpi/bus.c

  level:	4: subsys_initcall

  function:	init ACPI Bus Driver



#### 6 Bootchart & Generate SVG

* sudo vim /etc/default/grub 修改GRUB_CMDLINE_LINUX

* reboot
* ls /run/log

![d](C:\Users\Joe\Documents\Docs\Grade_3\OSP\Lab2\d.PNG)