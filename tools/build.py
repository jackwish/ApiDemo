#!/usr/bin/python3

#
# Copyright (C) 2017 王振华 (WANG Zhenhua) <i@jackwish.net>
#
# This program is free software: you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 3, as published
# by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranties of
# MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR
# PURPOSE.  See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#

import argparse
import subprocess
import os.path
import sys
import shutil

def main(args):
    apkname = 'apkdemo'
    target_pkg = 'bin/' + apkname + '-' + args.mode + '.apk'
    print(args)
    build_all(apkname, target_pkg, args)
    install_pkg(args.install, target_pkg, args.device)

def build_all(apkname, target_pkg, args):
    gen_java(apkname, args.sdk)
    build_binary(args.abi)
    build_package(apkname, target_pkg, args.mode)

def gen_java(apkname, sdk):
    cmd = 'android update project -n ' + apkname + ' -p . -t ' + sdk
    msg = 'Generating build configuration files'
    try_exec(cmd, msg)

def setup_jni_mk(abi):
    dest_mk = './jni/Application.mk'
    if os.path.exists(dest_mk):
        os.remove(dest_mk)
    src_mk = dest_mk + '.' + abi
    if os.path.exists(src_mk):
        shutil.copyfile(src_mk, dest_mk)
    else:
        print("copy Application.mk for JNI failed")
        exit(1)

def build_binary(abi):
    if os.path.exists('./jni'):
        setup_jni_mk(abi)
        cmd = 'ndk-build clean && ndk-build'
        msg = 'Building native binary'
        try_exec(cmd, msg)


def build_package(apkname, target_pkg, mode):
    cmd = 'ant clean'
    msg = 'Preparing package building'
    try_exec(cmd, msg)

    cmd = 'ant ' + mode
    msg = 'Building package ' + target_pkg
    try_exec(cmd, msg)

    sign_pkg(apkname, target_pkg, mode)


def sign_pkg(apkname, target_pkg, mode):
    if mode == 'debug': return
    print("Signing package...")

    unsigned_pkg = 'bin/' + apkname + '-' + mode + '-unsigned.apk'
    cmd = 'jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore ./tools/release.key -storepass 122333 ' + unsigned_pkg + ' test'
    try_exec(cmd)

    cmd = 'zipalign -v 4 ' + unsigned_pkg + ' ' + target_pkg
    try_exec(cmd)

def install_pkg(install, target_pkg, device):
    if not install: return
    if not device:
        print("No device connected!")
        exit()
    cmd = 'adb install -r ' + target_pkg
    msg = "Installing package " + target_pkg
    try_exec(cmd, msg)

def try_exec(cmd, msg=None):
    if msg: print(msg + '...', end='')
    status, output = subprocess.getstatusoutput(cmd)
    if status != 0:
        if msg: print(' FAIL')
        print(output)
        sys.exit(1)
    else:
        if msg: print(' OK')

def get_default_sdk():
    cmd = 'android list target -c | tail -n 1'
    return subprocess.getoutput(cmd)

def get_devices():
    cmd = 'adb devices | grep device | tail -n +2 | cut -f1'
    devices = subprocess.getoutput(cmd)
    devices = devices.split()
    device = None
    if len(devices) == 0 :
        print("No device connected, please check!\n")
    else:
        device = devices[0]
    return (device, devices)

def parse_args():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    sdk = get_default_sdk()
    device, devices = get_devices()
    parser.add_argument('-s', '--sdk',      help='provide a SDK version', default=sdk)
    parser.add_argument('-a', '--abi',      help='specify ABI of target device', choices=['arm', 'x86'], default='arm')
    parser.add_argument('-m', '--mode',     help='the mode of target application', choices=['debug', 'release'], default='debug')
    parser.add_argument('-i', '--install',  help='whether to install after build', choices=[True, False], type=bool, default=True)
    parser.add_argument('-d', '--device',   help='specify the device to install package', choices=devices, default=device)
    return parser.parse_args()

if __name__ == '__main__':
    args = parse_args()
    main(args)
