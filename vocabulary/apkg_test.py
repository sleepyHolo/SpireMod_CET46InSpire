# -*- coding: utf-8 -*-

import re
import json
import zipfile
import sqlite3
from bs4 import BeautifulSoup

misc = ['</font>', '</br>', '\r', '\n', '<br />', '<br>']
cet4_pattern = ["<div style='color:BlueViolet;text-align:center;font-size:16px;'>",
                "<font style='color:#c4151b;margin-right:.2em;font-weight:bold;font-style:italic;'>"]

def extract_file(zip_path: str, filename: str) -> bool:
    with zipfile.ZipFile(zip_path, 'r') as zip_ref:
        if filename in zip_ref.namelist():
            zip_ref.extract(filename, './')
            print(f'Unzipped file: {filename}')
            return True
        print(f'No such file: {filename}')
        return False

def select_all(db_path: str) -> None:
    with sqlite3.connect(db_path) as conn:
        cursor = conn.cursor()
        cursor.execute('SELECT * FROM notes')
        rows = cursor.fetchall()
    return rows

def parse(note: tuple) -> list:
    word = [note[7]]
    description = note[6]
    word.extend(meaning_parse(description, cet4_pattern[0], cet4_pattern[1]))
    return word

def meaning_parse(html: str, meaning: str, speech: str) -> list:
    html = html.replace('"', "'")
    if not (meaning_start := re.search(meaning, html)):
        return meaning_parse_2(html)
    meaning_end = re.search('</div>', html[meaning_start.end():])
    meaning = html[meaning_start.end():meaning_start.end()+meaning_end.start()]
    meanings = meaning.split(speech)
    return post_parse(meanings)

def meaning_parse_2(html: str) -> list:
    soup = BeautifulSoup(html, 'html.parser')
    all_a = soup.findAll('a')
    all_meaning = []
    for a in all_a:
        if a.get('href'):
            continue
        if not a.get('class'):
            continue
        all_meaning.append(a)
    del all_a
    # get meaning str
    all_meaning.reverse()
    if all_meaning == []:
        return ['ParseErr']
    tag = all_meaning.pop()
    html = html.replace('"', "'")
    tag_str = str(tag).replace('"', "'")
    html = tag.text + ' ' + html[re.search(tag_str, html).end():]
    html = html[:re.search('\u001F', html).start()]
    if all_meaning == []:
        return post_parse([html])
    meanings = []
    while all_meaning:
        tag = all_meaning.pop()
        match = re.search(str(tag).replace('"', "'"), html)
        if not match:
            print(str(tag))
            print(html)
            raise Exception
        meanings.append(html[:match.start()])
        html = tag.text + ' ' + html[match.end():]
    return post_parse(meanings)

def post_parse(meanings: list) -> list:
    ans = []
    for meaning in meanings:
        if not meaning:
            continue
        for type_ in misc:
            meaning = ''.join(meaning.split(type_))
        
        meaning = meaning.replace('&lt;', '<').replace('&gt;', '>').replace('&amp;', '&')
        meaning = '\n'.join(split_string(meaning))
        ans.append(meaning)
    return ans

def split_string(s: str, max_length: int = 16):
    length = len(s)
    if length <= max_length:
        return [s]
    num_segments = (length + max_length - 1) // max_length
    segment_length = (length + num_segments - 1) // num_segments
    segments = [s[i:i + segment_length] for i in range(0, length, segment_length)]
    return segments

def toUIString(data: list, info: str) -> dict:
    vocabulary_size = len(data)
    obj = {f'CET46:{info}_info': {'TEXT': [str(vocabulary_size)]}}
    for id_, item in enumerate(data):
        key_ = f'CET46:{info}_{id_}'
        obj[key_] = {'TEXT': item}
    return obj

def save(file_path: str, data: dict) -> None:
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(json.dumps(data, indent=4, ensure_ascii=False))
    
if __name__ == '__main__':
    zip_path = 'cet6.apkg'
    target = 'collection.anki21'
    if not extract_file(zip_path, target):
        raise Exception()
    tmp = select_all(target)
    a = [parse(note) for note in tmp]
    save('CET6.json', toUIString(a, 'CET6'))
    
        
    