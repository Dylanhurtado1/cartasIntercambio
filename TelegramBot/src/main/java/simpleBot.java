import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class simpleBot extends TelegramLongPollingBot {

  @Override
  public void onUpdateReceived(Update update) {
    System.out.println(update.getMessage().getText());
    System.out.println(update.getMessage().getFrom().getFirstName());

    String command = update.getMessage().getText();

    switch (command){
      case "/get_all_cards":
        System.out.println("en switch get_all_cards");
        break;

      case "/pokemon_cards":
        System.out.println("en switch pokemon_cards");
        break;
      case "/magic_cards":
        System.out.println("en switch magic_cards");
        break;

      default:
        System.out.println("No existe el comando");
    }
  }

@Override
public String getBotUsername(){
  return "TACS_1C2025_bot";
}

@Override
public String getBotToken(){
  return "8009117943:AAE_QWXkqx6_M68oByInvdtzPc6LM-5WDbs";
}


}