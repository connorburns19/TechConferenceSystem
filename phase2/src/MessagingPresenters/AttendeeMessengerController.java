package MessagingPresenters;

import Schedule.EventManager;
import UserLogin.MainMenuController;
import UserLogin.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * A class that represents the messenger controller.
 */

public class AttendeeMessengerController extends MessengerController {
    private final AttendeeMessengerPresenter presenter;
    public final AttendeeMessageManager messageManager;

    /**
     * A user is required to create an instance of this class.\
     */

    public AttendeeMessengerController(String attendeeEmail, Scanner scanner, MainMenuController mainMenuController,
                                       UserManager userManager, ConversationStorage conversationStorage, EventManager eventManager) {
        super(attendeeEmail, scanner, mainMenuController, userManager, conversationStorage, eventManager);
        messageManager = new AttendeeMessageManager(attendeeEmail, userManager, conversationStorage, eventManager);
        this.presenter = new AttendeeMessengerPresenter();
        this.userManager = userManager;
    }


    /**
     * Returns a list containing all recipients.
     *
     * @return an ArrayList containing all recipients
     */

    public ArrayList<String> getRecipients() {
        return messageManager.getRecipients();
    }

    public void run() {
        boolean flag = true;
        OUTER_LOOP:
        while (flag) {
            presenter.printMenu();
            int option = Integer.parseInt(scan.nextLine());
            try {
                if (option == 0) {
                    // QUIT
                    flag = false;
                    presenter.printQuitMessage();
                    mainMenuController.runMainMenu(email);
                }
                else if (option == 1) {
                    // VIEW INDIVIDUAL CHATS
                    ArrayList<String> emails = getRecipients();
                    presenter.viewChats(emails);
                    int index = Integer.parseInt(scan.nextLine());
                    if (index == 0 || emails.size() == 0) {
                        continue;
                    }
                    String email = emails.get(index - 1);
                    Boolean viewingArchivedMessages = false;
                    char input = 'a';
                    while (input != '0') {
                        ArrayList<Message> messages;
                        if (viewingArchivedMessages) {
                            messages = viewArchivedMessages(email);
                        }
                        else {
                            messages = viewUnarchivedMessages(email);
                        }
                        HashMap<String, String> messageMap = new HashMap<>();
                        for (Message message: messages) {
                            messageMap.put(message.getSenderEmail(), message.getMessageContent());
                        }
                        presenter.viewConversation(messageMap, viewingArchivedMessages);
                        input = scan.nextLine().toCharArray()[0];
                        if (input == 'a') {
                            viewingArchivedMessages = !viewingArchivedMessages;
                        }
                    }
                }
                else if (option == 2) {
                    //VIEW GROUP CHATS
                    ArrayList<String> talkIDS = getEventIDS();
                    presenter.viewGroupChats(talkIDS);
                    int index = Integer.parseInt(scan.nextLine());
                    if (index == 0 || talkIDS.size() == 0) {
                        continue;
                    }
                    String groupChatID = talkIDS.get(index - 1);
                    char input = 'a';
                    while (input != '0'){
                        ArrayList<String> messages = getGroupChatMessages(groupChatID);
                        presenter.viewGroupChat(messages);
                        input = scan.nextLine().toCharArray()[0];
                    }
                }
                else if (option == 3) {
                    // MESSAGE USER
                    presenter.askForEmail();
                    String email = "";
                    boolean valid_recipient = false;

                    while (!valid_recipient) {
                        email = scan.nextLine();
                        if (email.equals("0")) {
                            continue OUTER_LOOP;
                        }
                        if (messageManager.canMessage(email)) {
                            valid_recipient = true;
                        } else {
                            presenter.printSendMessageError();
                        }
                    }

                    presenter.askForMessageBody();
                    String body = scan.nextLine();
                    if (body.equals("0")) {
                        continue;
                    }

                    message(email, body);
                    presenter.printMessageSentSuccess();
                }
            }
            catch (NumberFormatException nfe) {
                presenter.printInvalidOptionError();
            }
        }
    }
}
