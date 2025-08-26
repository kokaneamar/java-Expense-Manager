//consider as pointer
import java.util.*;
import java.time.LocalDate;  //Live dates
import java.io.*;  //for saving loading a file

public class ExpenseManager {
    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Expense>expenses=new ArrayList<>(); //Array of variable type
    private static int idCounter=1;  //used for index or expense id

    //file named expenses.dat and put file in str called DATA_FILE
    //final is for constant i.e we cannot change file
    private static final String DATA_FILE = "expenses.dat";
    public static void main(String args[]){
        loadFromFile(); //load saved expenses (if any) before starting the menu loop
        boolean running = true;  //if true while runs otherwise exits if user choose 4
        while(running){
            showMenu();
            String choice = sc.nextLine();
            switch(choice){
                
                case "1":
                    addExpense();
                    break;

                case "2":
                    listExpenses();
                    break;

                case "3":
                    removeExpense();
                    break;

                case "4":
                    searchExpense();
                    break;
                 
                case "5":
                    running = false;
                    break;

                default:
                System.out.println("Invalid option - Choose 1-4.");
            }
        }
        System.out.println("Exiting..");
        saveToFile();  //file save on exit
        sc.close();
    }

    private static void showMenu(){
        System.out.println("\n=== Expense Manager ===");
        System.out.println("1) Add expense");
        System.out.println("2) List expenses");
        System.out.println("3) Remove expense by ID");
        System.out.println("4) Search expense");
        System.out.println("5) Exit");
        System.out.println("Choose: ");

    }

    private static void addExpense(){  //method
        System.out.print("Description: ");
        String desc=sc.nextLine();
        System.out.print("Amount: ");
        String amountStr = sc.nextLine();
        double amount;

        try{
            amount = Double.parseDouble(amountStr);  //convert string to double
        }catch(NumberFormatException e){
            System.out.println("Invalid amount. Aborting add.");
            return;
        }
        Expense e = new Expense(idCounter++,desc,amount,LocalDate.now());  //object 
        expenses.add(e);  // added new expense in arraylist expenses
        System.out.println("Added:"+e);

        saveToFile();  //save to disk after a successful change
    }

    private static void listExpenses(){
        if(expenses.isEmpty()){
            System.out.println("No Expenses yet.");
            return;
        }
        System.out.println("\nID | Description | Amount | Date");
        for(Expense e : expenses){  //for each loop
            System.out.println(e);
        }
    }

    private static void removeExpense(){
        System.out.print("Enter ID to remove: ");
        String idStr = sc.nextLine();
        int id;  //empty id to convert above str id into int
        try{
            id=Integer.parseInt(idStr);
        }catch(NumberFormatException ex){
            System.out.println("Invalid ID.");
            return;
        }
        Iterator<Expense> it = expenses.iterator();  //Expense is object as line 64
        while(it.hasNext()){
            Expense ex = it.next();
            if(ex.getID()==id){
                it.remove();
                System.out.println("Removed: "+ex);
                saveToFile();  //file saved
                return;
            }
        }
        System.out.println("No Expense found with ID "+ id);
    }
    
    private static void searchExpense(){
        System.out.println("Enter id to search:");
        String input = sc.nextLine();

        try{
            int id=Integer.parseInt(input);
            for(Expense e:expenses){
                if(e.getID()==id){
                    System.out.println("Found: " + e);
                    return;
                }
            }
            System.out.println("No expense found with id " + id);
        }catch(NumberFormatException ex){
            boolean found = false;
            for (Expense e : expenses) {
                if (e.Description.toLowerCase().contains(input.toLowerCase())) {
                     System.out.println("Found: " + e);
                     found = true;
            }
        }
        if (!found) {
            System.out.println("No expense found with keyword \"" + input + "\"");
        }
    }
}

    //objectoutputstream is aspecial java class that take whole arraylist and save it in file
    //new fileoutputstream(data file) opens the file new creates newone if doesnt exist
    //oos.writeObject(expenses) takes expenses from array expenses and write to file
    
    private static void saveToFile(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            oos.writeObject(expenses);
        }catch(IOException e){  //IOException is a type of error
            System.out.println("Failed to save data:" + e.getMessage());  //e.getmessage gives error details
        }
    }

    private static void loadFromFile(){
        //initially data file is just string we converted it to the file
        //!f is if f not exists
        File f = new File(DATA_FILE);
        if(!f.exists()){
            System.out.println("No saved Data found.Starting fresh.");
            return;
        }
        //Objectinputstream is wraps byte stream and gives power to read it

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if(obj instanceof ArrayList<?>) {  //checks what we read is Arraylist or not
               
                //take obj we read from file and treat it as arraylist of expenses
                expenses = (ArrayList<Expense>) obj;
                int maxId = 0; //made number called maxid set to 0
                //for each loop
                //for each expense e we look at its id if it greater than maxid we update maxid
                //because of this end of expenses we get largest id
                for(Expense e : expenses){
                    if(e.getID()>maxId) maxId=e.getID();
                }
                idCounter=maxId + 1;
                System.out.println("(Loaded " + expenses.size() + " expenses from disk.)");
              }
            }catch(IOException|ClassNotFoundException e){
              System.out.println("Failed to load saved data: " + e.getMessage());
            }  
        }


    private static class Expense implements Serializable{
        private static final long serialVersionUID = 1L;
        private int id;
        private String Description;
        private double amount;
        private LocalDate date;
        public Expense(int id, String Description, double amount, LocalDate date){
            this.id=id;
            this.Description=Description;
            this.amount=amount;
            this.date=date;
        }
        
        public int getID(){return id;}
        @Override
        public String  toString(){
            return id + " | " + Description + " | " + amount + " | " + date;
        }
    }
}
