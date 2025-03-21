# -*- coding: utf-8 -*-

# pip install urllib3==1.25.11

import re
import os
from pdf_test import read_pdf, save, toUIString

def init() -> None:
    with open('./prompt.txt', encoding='utf-8') as file:
        global api_key, prompt
        api_key = ''.join(file.readline().split('\n'))
        prompt = ''.join(file.readline().split('\n'))
        # openai 1.6.1
        # 改openai._base_client, BaseClient类(line 320)
        # (line 352) self._proxies = proxies  ->
        #            self._proxies = {'http': 'http://localhost:7890',
        #                             'https': 'http://localhost:7890'}
        # 没用
    #
    # os.environ['http_proxy'] = 'http://localhost:11434'
    # os.environ['https_proxy'] = 'http://localhost:11434'

def get_data(raw_data: list, except_: set,
             name_id: int, des_id: int, id_start: int = 0) -> list:
    index = id_start
    data: dict = {}
    des: list = []
    for word in raw_data:
        if word[0] in except_:
            continue
        data[index] = {}
        for id_, item in enumerate(word):
            if id_ == name_id:
                data[index]['NAME'] = item
            if id_ == des_id:
                des.append(item)
                data[index]['DESCRIPTIONS'] = ''
        index += 1
    return data, des

def get_data_parse(raw_data: list, except_: set,
             name_id: int, des_id: int, id_start: int = 0) -> list:
    index = id_start
    data: dict = {}
    for word in raw_data:
        if word[0] in except_:
            continue
        data[index] = {}
        for id_, item in enumerate(word):
            if id_ == name_id:
                data[index]['NAME'] = item
            if id_ == des_id:
                print(f'Parse: {index}')
                data[index]['DESCRIPTIONS'] = parse_DS_local_8b(item)
        index += 1
    return data

def parse_DS(meanings: list, prompt: str, max_num: int = 200) -> list:
    """这个似乎用不了,但是我找不到问题"""
    from openai import OpenAI
    client = OpenAI(api_key=api_key, base_url="https://api.deepseek.com")
    
    done: bool = False
    len_ = len(meanings)
    index = 0
    ans = []
    while not done:
        try:
            print(f'\nRound from: {index}', end='')
            if (len_ - index) > max_num:
                tmp = meanings[index: index + max_num]
            else:
                tmp = meanings[index:]
                done = True
            tmp = ''.join(';'.join(tmp).split('\n'))
            response = client.chat.completions.create(
                model='deepseek-chat',
                messages=[
                    {'role': 'system', 'content': prompt},
                    {'role': 'user', 'content': tmp},
                ],
                stream=False
            )
            print('    Round end', end='')
            ans.append(response.choices[0].message.content)
            index += max_num
        except Exception:
            import traceback
            print(f'\n\n======\n{traceback.format_exc()}')
            print(f'Index: {index}')
            return ans
    return ans

def init_ollama() -> None:
    import ollama
    global client
    client = ollama.Client(host='http://localhost:11434')

def parse_DS_local_8b(meaning: str) -> str:
    
    # make sure ollama is running
    # need to change http_proxy & https_proxy in init
    try:
        tmp = ''.join(';'.join(meaning).split('\n'))
        response = client.chat(
            model='deepseek-r1:8b',
            messages=[
                {'role': 'system', 'content': prompt},
                {'role': 'user', 'content': tmp},
            ],
            stream=False
        )
        tmp = remove_think(response['message']['content'])
        return tmp
    except Exception:
        import traceback
        print(f'\n\n======\n{traceback.format_exc()}')
        return tmp

def remove_think(input_: str) -> str:
    if (m := re.search('</think>', input_)):
        return input_[m.end():]

if __name__ == '__main__':
    init()
    init_ollama()
    file_path = r'.\大学英语四级词汇带音标-乱序版.pdf'
    # data, meanings = get_data(read_pdf(file_path, 1, 2), set(['序号']), 1, 3)
    # meanings = [''.join(item.split('\n')) for item in meanings]
    # a = parse_DS_local_8b(meanings, prompt, auto_save=True)
    # save('test.json', data)
    data = get_data_parse(read_pdf(file_path, 0, 1), set(['序号']), 1, 3)
    save('test_mean.json', data)
    
    
    