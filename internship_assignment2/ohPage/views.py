from django.shortcuts import render
from django.core.files.storage import FileSystemStorage
import requests,copy,random
from PIL import Image,ImageDraw
from time import sleep
import os,subprocess
# Create your views here.
"""
def index(request):
  if request.method=='POST':
    fs = FileSystemStorage()
    try:
      response_object=request.FILES['document']
      recieveJson=transfer(response_object)
      
      if (fs.exists(response_object.name)):
        fs.delete(response_object.name)
      fs.save(response_object.name, response_object)
      faceList,objList,posList = processJson(recieveJson)
      print(posList)
      #im=Image.open("C:/Users/gsm73/Desktop/Oh_web/mysite/static/media/"+response_object.name)
      #print(im.size)
      check_bit=False
      check_bit=imgDraw("C:/Users/gsm73/Desktop/Oh_web/mysite/static/media/"+response_object.name,posList)     
      while(not check_bit):
        return render(request,'index.html',context={'url':'/static/media/'+response_object.name[:-4]+"'_ver2.jpg'",'face':faceList,'obj':objList,'origindata':recieveJson})   
    except:
      pass
  else:
    return render(request,'index.html',context={'url':'','face':'','obj':'','origindata':''}) 
"""

def index(request):
  httpresponse=render(request,'index.html',context={'url':'','face':'','obj':'','origindata':''})
  if request.method=="POST":
    response_object=request.FILES['document']
    fs = FileSystemStorage()
    if (not fs.exists(response_object.name)):
      fs.save(response_object.name, response_object)
    try:
      recieveJson=transfer(response_object)
      faceList,objList,posList = processJson(recieveJson)
      imgDraw("C:/Users/gsm73/Desktop/web/mysite/static/media/"+response_object.name,posList)
      httpresponse=render(request,'index.html',context={'url':'/static/media/'+response_object.name[:-4]+'_ver2.jpg','face':faceList,'obj':objList,'origindata':recieveJson})  
    except:
      print("not excute")
  return httpresponse


def imgDraw(url, posList):
  img=Image.open(url)
  dctx = ImageDraw.Draw(img)
  for pos in posList:
    w, h = int(pos['w']), int(pos['h'])
    x, y= int(pos['x']),int(pos['y'])
    bbox = [(x, y), (w - 10, h - 10)]
    dctx.rectangle(bbox, outline="blue")
  img.save(url[:-4]+"_ver2.jpg")
  del dctx


def transfer(data):                       #image data 전송
  URL = 'http://210.217.95.183:8000/analyzer/'  
  files = {'image': open("C:/Users/gsm73/Desktop/web/mysite/static/media/"+data.name, 'rb')}
  parsedData=requests.post(URL, data={'modules':'missoh.face, object'} ,files=files)
  return parsedData.json()
  
def processJson(jsonData):
  faceList=[]
  objList=[]
  positionList=[]
  for instance in jsonData['results'][0]['module_result']:
    score=0.0
    description=""
    for label in instance['label']:
        if label['score']>score:
          score= label['score']
          description= label['description']
    faceList.append(description)
    positionList.append(instance['position'])
  for instance in jsonData['results'][1]['module_result']:
    score=0.0
    description=""
    for label in instance['label']:
        if label['score']>score:
          score= label['score']
          description= label['description']
    objList.append(description)
    positionList.append(instance['position'])
  
  resultFace=""
  resultObj=""
  for face in set(faceList):
    resultFace+=face+"\t"
  for obj in set(objList):
    resultObj+=obj+"\t"
  
  return resultFace,resultObj,positionList