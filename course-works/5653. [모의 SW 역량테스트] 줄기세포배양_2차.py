
dic = {0:[0,0],1:[0,-1],  2:[0,1],  3:[-1,0],   4:[1,0]}

def activateCheck(cell1,xytuple):
    x1,y1 = cell1.getLoc()
    cnt,sub_result=0,[]
    for i,j in ((0,0),(0,-1),(0,1),(-1,0),(1,0)):
        if (x1+i,y1+j) in xytuple:
            sub_result.append(cnt)
        cnt +=1
    return sub_result


class StemCell:
    def __init__(self, x,y,lifcnt):
        self.x, self.y = y,x
        self.lifcnt = lifcnt
        self.lif = lifcnt-1
    def countDown(self):
        if self.lifcnt > 0:
            self.lifcnt -= 1
    def islifover(self):
        return (True) if self.lifcnt==0 else (False)
    def getLoc(self):
        return self.x, self.y
    def expanding(self,loc,grid_dic ,cell_queue):
        grid_loc=list(grid_dic.keys())
        exlist= set(loc)
        tuple_list= []
        for i in range(len(grid_loc)):
            x,y=map(int , grid_loc[i].split(','))
            tmp = (x,y)
            tuple_list.append(tmp)
        exlist.update(activateCheck(self,tuple_list))
        cell_x,cell_y= self.getLoc()
        print("------------------------------")
        for i in range(5):
            j,k =dic[i]
            print( cell_x+j,cell_y+k ,i,not (i in  exlist))
            if not (i in  exlist):
                grid_dic[str(cell_x+j)+","+str(cell_y+k)] = self.lif
                cell_queue.append(StemCell(cell_x+j,cell_y+k,self.lif+1))
        return grid_dic
            
        
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

        for cell in self.cell_queue:
            cell.countDown()
            
            if cell.islifover()==True:
                countPop = countPop+1
        print("time processing",len(self.cell_queue))
        print("분화 할 셀 개수 ",countPop)
        if countPop == 0:
            pass
        else:
            self.cell_queue.sort(key = lambda object : object.lifcnt)   #활성큐 정렬
            if countPop ==1:
                (self.cell_queue.pop(0)).expanding([],self.grid_list,self.cell_queue)
            else :
                tmp_cell=[]

                for i in range(0,countPop):
                    tmp_cell.append(self.cell_queue.pop(0))
                tmp_cell.sort(reverse=True, key = lambda object : object.lif)
            
                for i in range(0,countPop):
                    cell=tmp_cell.pop(0)
                    exlist= set([])
                    tuple_list= []
                    for l in range(0,countPop-i-1):
                        x,y=tmp_cell[l].getLoc()
                    
                        for j,k in ((0,0),(0,-1),(0,1),(-1,0),(1,0)):
                            tuple_list.append((x+j,y+k))
                    exlist=activateCheck(cell,tuple_list)
                    cell.expanding(list(exlist),self.grid_list,self.cell_queue)
                    
            print(len(self.grid_list))


T = 1

for test_case in range(1, T + 1):
    N, M, K= 5, 5, 19
    #map(int, input().split())
    cl =Culture([[3, 2, 0, 3, 0],[0, 3, 0, 0, 0],[0, 0, 0, 0, 0],[0,0, 1, 0,0],[0, 0, 0, 0, 2]])
    #([list(map(int, input().split())) for _ in range(N)])
    for i in range(K):
        cl.timeProcessing()
    print(cl.getAns())
