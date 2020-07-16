package os;

import java.util.*;

class Process {
   private String name;               
   private int arrivalTime;            
   private int serviceTime; 
   private int executionTime;
   Process(String name, int arrivalTime, int serviceTime) { 
      this.name =name; this.arrivalTime =arrivalTime; this.serviceTime = serviceTime; executionTime = 0; }
   public String getName(){ return this.name; }
   public boolean isFinished(){return (serviceTime<=executionTime);}
   public void incExecTime(){ executionTime++; }
   public int getRemainingTime(){ return serviceTime-executionTime;} 
   public int getWaitingTime(int cTime){   return arrivalTime-cTime; }
}

class ProcessController {
   static public void reset(){ index = 0; }
   static private String processNames[] = { "A", "B", "C", "D", "E", "A", "B", "C", "D", "E" };      //SAMPLE
   static private int arrivalTimes[] = {   0,   2,   4,   6,   8, 30,   32,  34,  36,  38 };
   static private int serviceTimes[] = {   3,   6,   4,   5,   2,  6,    3,   4,   5,   2 };   
   static private int index;                         //NEXT PROCESS INDICATER
   
   static public boolean hasNextProcess() { return index < arrivalTimes.length; }          //IDEX < LAST INDEX?   

   static public Process checkNewProcessArrival(int currentTime) {      
      if ((index < arrivalTimes.length) &&  (arrivalTimes[index] == currentTime)) {
         int i = index++;                                    //POINTIN NEXT   
         return new Process(processNames[i], arrivalTimes[i], serviceTimes[i]);    //SAMPLE
      }
       return null; 
   } 
}


abstract class Scheduler {      //aspect of queue
   private String name; 
   protected int currentTime; 
   protected Process currentProcess;               //지금 하는 짓기록  포인터 not object
   protected boolean isNewProcessArrived;
   protected LinkedList<Process> readyQueue;
   Scheduler(String name) { 
      this.name = name; currentTime = -1; currentProcess = null;    isNewProcessArrived = false;
      readyQueue = new LinkedList<Process>();
      System.out.println(currentTime);
      ProcessController.reset();
   }
   public String getName() { return this.name; }       // Scheduler Algorism, not process
   public void addReadyQueue(Process p) { readyQueue.add(p);}
   public boolean isThereAnyProcessToExecute() {    return   (ProcessController.hasNextProcess() || (currentProcess != null) || !readyQueue.isEmpty());}    //야! 프로세스 남은놈 있나!?
   
   public void schedule() {
      if((currentProcess != null)) {
         if ( currentProcess.isFinished()) {   
            readyQueue.remove(currentProcess); 
            currentProcess = null;
      }}
   }
   public void clockInterrupt() {
      currentTime++;    // time check
      if (currentProcess != null) {   
         currentProcess.incExecTime();       //process's time update, and report current process
         System.out.print(currentProcess.getName());
         }
      else
         System.out.print("-");
      Process p = ProcessController.checkNewProcessArrival(currentTime);   //야 다음프로세스 올때 안됬나?
      isNewProcessArrived = (p != null);
      if (isNewProcessArrived)
         addReadyQueue(p);
   }
}

class SRT extends Scheduler { 
   SRT(String name) { super(name); } //  Scheduler 클래스의 생성자 호출
   @Override
   public void schedule() { 
      super.schedule();
      Process nextProcess = readyQueue.peek();                     // gready logic init   
      for (Process p: readyQueue) 
         if (p.getRemainingTime() < nextProcess.getRemainingTime())
          nextProcess = p;   //remaining time is least  that is power
      currentProcess = nextProcess; 
 
   }
   @Override
   public void clockInterrupt() {   
      super.clockInterrupt(); 
      if ( isNewProcessArrived || ((currentProcess != null) && currentProcess.isFinished()) )   
         schedule();
      }
   
}

class RR extends Scheduler {
   private int quantum;
   private int execTime;    //RR LIFE TIME
   RR(String name, int qauntum) { 
      super(name);       //receive process
      execTime = 0;      
      this.quantum = qauntum;   //life segment, standard
   }
   @Override
   public void schedule() { 
      super.schedule();
      if(currentProcess == null){
         try {
         currentProcess = readyQueue.pop();
         execTime = 0 ;
         }catch(NoSuchElementException e) {}
      }

      if(execTime>=quantum) {
         readyQueue.add(currentProcess);
//         System.out.println("\n현재프로세스 미루기");
         currentProcess = null;
         try {
                currentProcess = readyQueue.pop();
                execTime = 0 ;
                }catch(NoSuchElementException e) {}
     }


   }

   @Override
   public void clockInterrupt() {
      super.clockInterrupt();
      execTime++;
//      try {
//         System.out.printf("\n%d, %d, ",currentTime, execTime);
//         for(Process p: readyQueue) {
//            try {
//            System.out.printf(" %s ",p.getName());
//            }catch(NullPointerException e) {}
//         }
//         System.out.println();
//         }catch(NoSuchElementException e) {}
      if ((currentProcess != null && currentProcess.isFinished())|| execTime>=quantum || isNewProcessArrived )   
         schedule();
   }
}

public class SchedulingApp_20171103 {
   public static void printEpilog(Scheduler scheduler) {
      System.out.printf("Scheduling Algorithm: %s\n", scheduler.getName() );
      System.out.println("0\t 1\t 2\t 3\t 4\t 5" );                   // 시간 십단위
      System.out.println("012345678901234567890123456789012345678901234567890 1234");    // 시간 일단위   //단위시간 눈금자
   }
   
   public static void schedAppRun(Scheduler scheduler) {    //main 목적지
      printEpilog(scheduler);          
      while (scheduler.isThereAnyProcessToExecute()) {
         scheduler.clockInterrupt();		 //스케줄링 발생하는 위치
         try {                           
            Thread.sleep(100);          // 100 millisecond 동안 대기상태, 즉 100ms마다 한번씩 위 scheduler.clockInterrupt()가 호출됨
         }
         catch (InterruptedException e) {     
            e.printStackTrace(); 
            return;
         }
      }
      System.out.println("\n");
   }
   public static void main(String[] args) {      
     schedAppRun(new SRT("SRT"));    
      schedAppRun(new RR("RR q=1", 1));
      schedAppRun(new RR("RR q=4", 4));
   }
}