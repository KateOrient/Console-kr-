package Console;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.stream.Collectors;

public class Console{
    private static Logger LOGGER = Logger.getLogger(Console.class.getName());
    //настраиваем Logger (хотим, чтобы не выводил на консоль, а выводил в файл в указанном нами формате в классе MyFormatter)
    static {
        LOGGER.setUseParentHandlers(false);
        try{
            FileHandler fh = new FileHandler("logfile.txt");
            fh.setFormatter(new MyFormatter());
            LOGGER.addHandler(fh);
        }catch (IOException e){
            System.err.print("Logger error");
        }
    }

    private List<String> commands;

    public Console (){
        commands = new ArrayList<>();
    }

    public void loadFromFile (String fileName) throws IOException{
        Scanner scanner = new Scanner(new File(fileName)).useDelimiter("\\s*;\\s*"); //считывать с файла будем разделяя сразу на команды и обрезая ненужные пробелы по краям
        String tmp;
        while (scanner.hasNext()){
            if((tmp = scanner.next()).contains("cd")){
                tmp = tmp.replaceFirst("cd\\s+","cd "); //между командой cd и ее аругментом оставляем только 1 пробел вместо возможных нескольких
            }
            commands.add(tmp);
        }
    }

    public void printSortedByLenght (String fileName){
        //сортируем список с командами с помощью анонимного класса-компаратора и складываем в новый список, чтобы не портить исходный
        List<String> tmpList = commands.stream().sorted(new Comparator<String>(){
            @Override
            public int compare (String o1, String o2){
                return o2.length() - o1.length();
            }
        }).collect(Collectors.toList());

        try{
            //записываем в файл каждую команду из отсортированного списка, используя итератор
            PrintStream ps = new PrintStream(fileName);
            ListIterator<String> it = tmpList.listIterator();
            while (it.hasNext()){
                ps.println(it.next());
            }
        }
        catch (IOException e){
            System.err.println("Error");
        }
    }

    public void printProcessDir(String fileName)throws IOException{
        //по умолчанию для команды dir текущая папка C:\
        File directory = new File("C:\\");
        PrintStream ps = new PrintStream(fileName);
        //поочередно выполняем команды из списка
        for(String item:commands){
            //если текущая команда dir
            if(item.startsWith("dir")){
                if(directory.exists()){  //если такая папка вообще существует
                    ps.println(directory.getPath()); //выводим путь к папке, в которой находимся сейчас
                    for (String file : directory.list()){    //и все файлы и папки из нее
                        ps.println(file);
                    }
                    ps.println();
                }
            }
            //если текущая команда cd..
            else if(item.startsWith("cd..")){
                if(directory.getParent()!=null){ //и мы можем выйти на уровнь выше
                    directory = new File(directory.getParent()); //то выходим
                    LOGGER.info(item+"\nКоманда выполнена успешно."); //и выводим сообщение об успешном выполнении команды cd
                }
                else{  //если выйти на уровень выше невозможно, то выводим сообщение о том, что не удалось выполнить команду
                    LOGGER.warning(item+"\nНе удалось выйти на уровень выше.");
                }
            }
            //если текущяя команда cd
            else if(item.startsWith("cd")){
                //переходим в папку указанную в команде, вырезая из строки команды только путь к папке
                directory = new File(item.replaceFirst("cd ",""));
                if(directory.exists()){  //если такая папка существует
                    LOGGER.info(item + "\nКоманда выполнена успешно."); //выводим сообщение об успешном выполненеи команды
                }
                else{ //если открыть папку невозможно, то выводим сообщение о том, что не удалось выполнить команду
                    LOGGER.warning(item+"\nНе удалось открыть указанную папку.");
                }
            }
        }
    }
}

//класс для форматирования вывода сообщений классом Logger
class MyFormatter extends Formatter{
    @Override
    public String format(LogRecord record){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        StringBuilder sb  = new StringBuilder();
        sb.append(record.getMessage()).append("\n");
        return sb.toString();
    }

}

