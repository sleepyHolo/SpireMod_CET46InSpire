# -*- coding: utf-8 -*-

import re
import json
import pdfplumber

type_list = ['n.', 'v.', 'vi.', 'vt.', 'adj.', 'adv.', 'prep.', 'conj.']

def get_data(raw_data: list, except_: set,
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
                data[index]['DESCRIPTIONS'] = parse_meaning(item)
        index += 1
    return data
            
def parse_meaning(meaning: str) -> list:
    target = []
    for type_ in type_list:
        if (match_pos := re.search(type_, meaning)):
            target.append(match_pos.start())
    target.sort()
    target.append(len(meaning))
    ans = []
    for i in range(len(target) - 1):
        ans.append(meaning[target[i]: target[i + 1]])
    return meaning_format(ans)

def meaning_format(meanings: list) -> list:
    tmp = []
    for meaning in meanings:
        if not meaning:
            continue
        tmp.append(','.join(meaning.rstrip().split(' ')))
    ans = []
    for tmp_l in tmp:
        a = ''.join(tmp_l.split('\n'))
        if (l := len(a)) > 16:
            a = a[:l//2] +'\n' + a[l//2:]
        ans.append(a)
    return ans

def read_pdf(file_path: str) -> list:
    with pdfplumber.open(file_path) as pdf:
        tables = []
        for page in pdf.pages:
            tables.extend(page.extract_table())
        # tables.extend(pdf.pages[0].extract_table())
        # tables.extend(pdf.pages[1].extract_table())
    return tables

def save(file_path: str, data: dict) -> None:
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(json.dumps(data, indent=4, ensure_ascii=False))

def toUIString(data: dict, info: str) -> dict:
    vocabulary_size = len(data.keys())
    obj = {f'CET46:{info}_info': {'TEXT': [str(vocabulary_size)]}}
    for key in data:
        item = data[key]
        key_ = f'CET46:{info}_{key}'
        obj[key_] = {'TEXT': [item['NAME']]}
        obj[key_]['TEXT'].extend(item['DESCRIPTIONS'])
    return obj
    
if __name__ == '__main__':  
    file_path = r'.\大学英语四级词汇带音标-乱序版.pdf'
    a = read_pdf(file_path)
    a = get_data(a, set(['序号']), 1, 3)
    save('CET4.json', toUIString(a, 'CET4'))
    
    
    
