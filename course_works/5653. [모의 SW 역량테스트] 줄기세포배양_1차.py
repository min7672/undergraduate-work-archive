dic = {0:[0,0],1:[0,-1],  2:[0,1],  3:[-1,0],   4:[1,0]}

def activateCheck(cell1,x,y):
    x1,y1 = cell1.getLoc()
    cnt,sub_result=0,[]
    for i,j in ((0,0),(0,-1),(0,1),(-1,0),(1,0)):
        if [x1+i,y1+j] == [x,y]:
            sub_result.append(cnt)
        cnt +=1
    return sub_result


class StemCell:
    def __init__(self, x,y,lifcnt):
        self.x, self.y = y,x
        self.lifcnt = lifcnt
        self.lif = lifcnt-1
        #print(self.x,self.y)
    def countDown(self):
        if self.lifcnt >0:
            self.lifcnt -= 1
    def islifover(self):
        return (True) if self.lifcnt==0 else (False)
    def getLoc(self):
        return self.x, self.y
    def expanding(self,loc,grid_dic ,cell_queue):
        grid_loc=list(grid_dic.keys())
        exlist= set(loc)
        #print(grid_dic)
        for i in range(len(grid_loc)):
            #print(type(grid_loc[i]))
            x,y=map(int , grid_loc[i].split(','))
            exlist.update(activateCheck(self,x,y))
            cell_x,cell_y= self.getLoc()
        for i in range(5):
            #print(i,i in exlist)
            if not (i in  exlist):
                j,k =dic[i]
                #print( j, k)
                grid_dic[str(cell_x+j)+","+str(cell_y+k)] = self.lif
                cell_queue.append(StemCell(cell_x+j,cell_y+k,self.lif+1))
        return grid_dic
            
        
#[0,0],[0,-1],[0,1][-1,0],[1,0]: #0,1(아래),2(위),3(좌),4(우)
        
class Culture:
    def __init__(self, grid_input):
        self.grid_list ={}
        self.cell_queue =[]
        for i in range(len(grid_input)):
            for j in range(len(grid_input[i])):
                if grid_input[i][j] != 0:
                    self.grid_list[str(j)+","+str(i)]= grid_input[i][j]
                    self.cell_queue.append(StemCell(i,j,grid_input[i][j]+1))
    def getAns(self):
        return len(self.cell_queue)
    def timeProcessing(self):
        countPop = 0
        #print("time running")
        #print(self.cell_queue)
        #print(self.grid_list)
        self.cell_queue.sort(key = lambda object : object.lifcnt)   #활성큐 정렬
        for cell in self.cell_queue:
            cell.countDown()
            
            #print(cell.islifover())
            if cell.islifover()==True:
                #print("체킹안함?")
                countPop = countPop+1
            
        #print(countPop)

        if countPop == 0:
            pass
        elif countPop ==1:
            (self.cell_queue.pop(0)).expanding([],self.grid_list,self.cell_queue)
            #print(self.grid_list)
            
        else:
            tmp_cell=[]
            
            for i in range(0,countPop):
                tmp_cell.append(self.cell_queue.pop(0))
            tmp_cell.sort(reverse=True, key = lambda object : object.lif)
            
            for i in range(0,countPop):
                cell=tmp_cell.pop(0)
                exlist= set([])
                for l in range(0,countPop-i-1):
                    x,y=tmp_cell[l].getLoc()
                    
                    for j,k in ((0,0),(0,-1),(0,1),(-1,0),(1,0)):
                        exlist.update(activateCheck(cell,x+j,y+k))
                cell.expanding(list(exlist),self.grid_list,self.cell_queue)
                #print(self.grid_list)
                
            #[0,0],[0,-1],[0,1][-1,0],[1,0]: #0,1(아래),2(위),3(좌),4(우)
            
    

T = int(input())

for test_case in range(1, T + 1):
    N, M, K= map(int, input().split())
    grid_data = [list(map(int, input().split())) for _ in range(N)]
    cl =Culture(grid_data)
    for i in range(K):
        cl.timeProcessing()
    print(cl.getAns())
