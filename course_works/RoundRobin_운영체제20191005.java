package course_works;

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
   protected Process currentProcess;             
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
   public boolean isThereAnyProcessToExecute() {    return   (ProcessController.hasNextProcess() || (currentProcess != null) || !readyQueue.isEmpty());}    //��! ���μ��� ������ �ֳ�!?
   
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
      Process p = ProcessController.checkNewProcessArrival(currentTime);  
      isNewProcessArrived = (p != null);
      if (isNewProcessArrived)
         addReadyQueue(p);
   }
}

class SRT extends Scheduler { 
   SRT(String name) { super(name); }
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

public class RoundRobin_운영체제20191005 {
   public static void printEpilog(Scheduler scheduler) {
      System.out.printf("Scheduling Algorithm: %s\n", scheduler.getName() );
      System.out.println("0\t 1\t 2\t 3\t 4\t 5" ); 
      System.out.println("012345678901234567890123456789012345678901234567890 1234");
   }
   
   public static void schedAppRun(Scheduler scheduler) {
      printEpilog(scheduler);          
      while (scheduler.isThereAnyProcessToExecute()) {
         scheduler.clockInterrupt();
         try {                           
            Thread.sleep(100);     
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