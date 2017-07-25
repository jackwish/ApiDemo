#!/usr/bin/python3

import argparse
import subprocess
import os.path
import sys

def main(args):
    apkname = 'apkdemo'
    print(args)
    build_all(apkname, args.sdk, args.mode)
    install_pkg(args.install, apkname, args.mode)

def build_all(apkname, sdk, mode):
    gen_java(apkname, sdk)
    build_binary()
    build_package(apkname, mode)

def gen_java(apkname, sdk):
    cmd = 'android update project -n ' + apkname + ' -p . -t ' + sdk
    msg = 'Generate build configuration files'
    try_exec(cmd, msg)

def build_binary():
    if os.path.exists('./jni'):
        cmd = 'ndk-build clean && ndk-build'
        msg = 'Build native binary'
        try_exec(cmd, msg)


def build_package(apkname, mode):
    cmd = 'ant clean'
    msg = 'Preparing package building'
    try_exec(cmd, msg)

    cmd = 'ant ' + mode
    msg = 'Build package'
    try_exec(cmd, msg)

    sign_pkg(apkname, mode)


def sign_pkg(apkname, mode):
    if mode == 'debug': return

    target_pkg = 'bin/' + apkname + '-' + mode + '.apk'
    cmd = 'jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore ./tools/release.key ' + target_pkg + ' test'
    try_exec(cmd)

    unsigned_pkg = target_pkg
    signed_pkg = 'bin/' + apkname + '-' + mode + '.apk'
    cmd = 'zipalign -v 4 ' + unsigned_pkg + ' ' + signed_pkg
    try_exec(cmd)

def install_pkg(install, apkname, mode):
    if not install: return
    check_device()
    pkg_name = 'bin/' + apkname + '-' + mode + '.apk'
    cmd = 'adb install -r ' + pkg_name
    msg = "Install package " + pkg_name
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
    device = "No device!"
    if len(devices) == 0 :
        print("No device connected, please check!\n")
    else:
        device = devices[0]
    return (device, devices)

def parse_args():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    sdk = get_default_sdk()
    root_dir = os.path.dirname(os.getcwd())
    key_path = os.path.join(root_dir, 'tools/release.key')
    device, devices = get_devices()
    parser.add_argument('-s', '--sdk',      help='provide a SDK version', default=sdk)
    parser.add_argument('-a', '--abi',      help='specify ABI of target device', choices=['armeabi', 'x86'], default='armeabi')
    parser.add_argument('-m', '--mode',     help='the mode of target application', choices=['debug', 'release'], default='debug')
    parser.add_argument('-i', '--install',  help='whether to install after build', choices=[True, False], type=bool, default=True)
    parser.add_argument('-d', '--device',   help='specify the device to install package', choices=devices, default=device)
    parser.add_argument('-p', '--path',     help='root dir of the apk', default=root_dir)
    parser.add_argument('-k', '--key',      help='specify the key for release package', default=key_path)
    return parser.parse_args()

if __name__ == '__main__':
    args = parse_args()
    main(args)
