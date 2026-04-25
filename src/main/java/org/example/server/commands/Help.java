package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;

public class Help extends Command{

    public Help(){
        super("help");
    }

    @Override
    public Response execute(Request request, User user) {
        String help = """
                Доступные команды:
                help : вывести справку по доступным командам
                info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
                show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
                add {element} : добавить новый элемент в коллекцию
                update id {element} : обновить значение элемента коллекции, id которого равен заданному
                remove_by_id id : удалить элемент из коллекции по его id
                clear : очистить коллекцию
                execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
                exit : завершить программу (без сохранения в файл)
                remove_head : вывести первый элемент коллекции и удалить его
                remove_greater {element} : удалить из коллекции все элементы, превышающие заданный
                history : вывести последние 6 команд (без их аргументов)
                remove_any_by_official_address officialAddress : удалить из коллекции один элемент, значение поля officialAddress которого эквивалентно заданному
                print_descending : вывести элементы коллекции в порядке убывания
                print_field_descending_type : вывести значения поля type всех элементов в порядке убывания
                """;
        return new Response(true, help, null);
    }
}
