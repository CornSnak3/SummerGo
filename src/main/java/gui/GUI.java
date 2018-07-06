package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    private JMenuBar jMenuBar;
    private JMenu jMenuGame;
    private JMenu jMenuHelp;
    private JMenu jMenuAbout;



    // Game Menu
    private JMenuItem jGameNew;
    private JMenuItem jGameSave;
    private JMenuItem jGameLoad;
    private JMenuItem jGameExit;

    public GUI() {
        initMenu();
    }

    void initMenu() {
        jMenuBar = new JMenuBar();

        jMenuGame = new JMenu();
        jMenuHelp = new JMenu();
        jMenuAbout = new JMenu();

        // Game Menu
        jGameNew = new JMenuItem();
        jGameNew.setAccelerator(KeyStroke.getKeyStroke("control N"));
        jGameNew.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent actionEvent) {
                                           String[] boardSizeOptions = { "9x9", "13x13", "19x19" };
                                           String boardSizeChoose = (String) JOptionPane.showInputDialog(jMenuGame,
                                                   "Choose board size",
                                                   "New game",
                                                   JOptionPane.QUESTION_MESSAGE,
                                                   null,
                                                   boardSizeOptions,
                                                   boardSizeOptions[0]);

                                           try {
                                                int boardSize = Integer.parseInt(boardSizeChoose.split("x")[0]);
                                                //TODO new game start
                                                System.out.println("New Game");
                                           } catch(Exception ex) {

                                           }

                                       }
                                   });
        jGameSave = new JMenuItem();
        jGameLoad = new JMenuItem();
        jGameExit = new JMenuItem();
    }

}
