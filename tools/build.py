#!/usr/bin/python3

import argparse
import subprocess
import os.path
import sys

def main(args):
    apkname = 'apkdemo'
    print(args)
    cleanup(args.clean)
    build_all(apkname, args.sdk, args.mode)
    install_pkg(args.install, apkname, args.mode)

def cleanup(will_exit):
    tmp_files = ['bin' 'gen' 'libs' 'obj' 'build.xml' 'local.properties' 'proguard-project.txt'  'project.properties']
    for f in tmp_files:
        if os.path.isdir(f):
            subprocess.getoutput('rm -rf ' + f)
        else:
            subprocess.getoutput('rm -f ' + f)
    if will_exit:
        sys.exit('files cleanup done!')

def build_all(apkname, sdk_ver, mode):
    gen_java(apkname, sdk_ver)
    build_binary()
    build_package(apkname, mode)

def gen_java(apkname, sdk_ver):
    cmd = 'android update project -n ' + apkname + ' -p . -t ' + sdk_ver
    msg = 'Generate build configuration files'
    try_exec(cmd, msg)

def build_binary():
    if os.path.exists('./jni'):
        cmd = 'ndk-build clean && ndk-build'
        msg = 'Build native binary'
        try_exec(cmd, msg)


def build_package(apkname, mode):
    # cmd = 'ant clean' + work_before_pkg()
    cmd = 'ant clean'
    msg = 'Preparing package building'
    try_exec(cmd, msg)

    cmd = 'ant ' + mode
    msg = 'Build package'
    try_exec(cmd, msg)

    sign_pkg(apkname, mode)


def work_before_pkg():
    return

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

def check_device():


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

def parse_args():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    sdk_ver = get_default_sdk()
    root_dir = os.path.dirname(os.getcwd())
    key_path = os.path.join(root_dir, 'tools/release.key')
    device = 
    parser.add_argument('-s', '--sdk',      help='provide a SDK version', default=sdk_ver)
    parser.add_argument('-a', '--abi',      help='specify ABI of target device', choices=['armeabi', 'x86'], default='armeabi')
    parser.add_argument('-m', '--mode',     help='the mode of target application', choices=['debug', 'release'], default='debug')
    parser.add_argument('-i', '--install',  help='whether to install after build', choices=[True, False], type=bool, default=True)
    parser.add_argument('-c', '--clean',    help='exit after clean up', choices=[True, False], type=bool, default=False)
    parser.add_argument('-p', '--path',     help='root dir of the apk', default=root_dir)
    parser.add_argument('-k', '--key',      help='specify the key for release package', default=key_path)
    parser.add_argument('-d', '--device',   help='specify the device to install package', default=key_path)
    return parser.parse_args()

if __name__ == '__main__':
    args = parse_args()
    main(args)
