@echo off
setlocal enabledelayedexpansion

REM 定义 IP 和文件路径变量
set "IP=192.168.1.3"
REM set "FILE_PATH=./readme.md"

REM set "IP=%1"
set "FILE_PATH=%1"


REM 激活虚拟环境
call "D:\softwares\Anaconda3\Scripts\activate"

REM 调用 Python 脚本并传递参数
python push_file.py push -si !IP! -fp !FILE_PATH!

