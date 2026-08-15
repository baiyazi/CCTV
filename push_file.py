"""
    @Date: 2023/12/31
    @Author: 梦否
    @Blog: https://mengfou.blog.csdn.net/
    @Info[功能说明]:  
"""

import requests
import argparse
import os


def doRequest(file_path, server_ip):
    headers = {
        'Origin': f'http://{server_ip}:1111',
        'Referer': f'http://{server_ip}:1111/',
        'Accept-Encoding':'gzip, deflate',
        'Accept-Language':'zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7',
        'Connection':'keep-alive',
        'X-Requested-With':'XMLHttpRequest',
        'Accept':'application/json',
        'Host':f'{server_ip}:1111'
    }

    target_index = 0
    for i in range(len(file_path)):
        if file_path[i] == '/':
            target_index = i
    file_name = file_path[target_index:]


    if not os.path.exists(file_path):
        raise Exception(f"file {os.path.abspath(file_path)} not exist!")

    files = {
        'file':(file_name, open(file_path, 'rb'), 'application/octet-stream')
    }

    url = f'http://{server_ip}:1111/upload'
    print(f"url: {url}")
    response = requests.post(url=url, headers=headers, files=files)
    
    if response.status_code == 200:
        print('upload file success')
    else:
        print('upload file fail')
    
    print('error message：', response.text)



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="push file to toupin.")
    subparsers = parser.add_subparsers(dest='command')
    parser_push = subparsers.add_parser('push', help='push file to toupin.')
    parser_push.add_argument('-si', '--server-ip', required=True, help='local server ip')
    parser_push.add_argument('-fp', '--file-path', required=True, help='ready to push toupin sys file path.')

    args = parser.parse_args()

    if args.command == 'push':
        print(f"push file {args.file_path} to server {args.server_ip}")
        print(f'build request...')
        doRequest(args.file_path, args.server_ip)