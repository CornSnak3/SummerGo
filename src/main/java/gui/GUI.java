package gui;

import core.Board;
import core.exception.UnsupportedFileFormatException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {

    public final int TOKEN_INITIAL_SIZE = 35;
    public final int MENU_SIZE = 42;
    public final int CONTROLLER_PANEL_WIDTH = 610;
    public final int BOARD_PANEL_DIMENSION = 706 - 10;

    private final Dimension BOARD_PANEL = new Dimension(BOARD_PANEL_DIMENSION, BOARD_PANEL_DIMENSION);

    private JPanel jBoard;
    private JPanel jStartScreenPanel;

    private JPanel jUtility;
    JLabel blackPlayerCaptures;
    JLabel whitePlayerCaptures;

    private JMenuBar jMenuBar;
    private JMenu jMenuGame;
    private JMenu jMenuHelp;
    private JMenu jMenuAbout;

    // Game Menu
    private JMenuItem jGameNew;
    private JMenuItem jGameSave;
    private JMenuItem jGameLoad;
    private JMenuItem jGameExit;

    private final int WINDOW_WIDTH = 1366;
    private final int WINDOW_HEIGHT = 768 - MENU_SIZE;

    private Board board;
    private boolean isGameGoing;

    private JButton[][] jIntersections;

    public GUI() {
        this.setLayout(new GridBagLayout());
        isGameGoing = false;
        initMenu();
        initWindow();
        showStartScreen();
    }

    void initWindow() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Summer Go");
        setSize(new Dimension(1366, 768));
        setResizable(false);
        setVisible(true);
    }

    void showStartScreen() {
        GridLayout startScreenLayout = new GridLayout(3, 1, 0, 50);
        
        jStartScreenPanel = new JPanel(startScreenLayout);

        Dimension startScreenButtonDimension = new Dimension(200, 75);

        JButton jNewGameButton = new JButton("New Game");
        jNewGameButton.addActionListener(getNewGameListener());
        jNewGameButton.setMinimumSize(startScreenButtonDimension);
        jNewGameButton.setPreferredSize(startScreenButtonDimension);
        jStartScreenPanel.add(jNewGameButton);

        JButton jLoadGameButton = new JButton("Load Game");
        jLoadGameButton.addActionListener(getLoadGameListener());
        jLoadGameButton.setMinimumSize(startScreenButtonDimension);
        jLoadGameButton.setPreferredSize(startScreenButtonDimension);
        jStartScreenPanel.add(jLoadGameButton);

        JButton jExitGameButton = new JButton("Exit Game");
        jExitGameButton.addActionListener(getExitGameListener());
        jExitGameButton.setMinimumSize(startScreenButtonDimension);
        jExitGameButton.setPreferredSize(startScreenButtonDimension);
        jStartScreenPanel.add(jExitGameButton);

        getContentPane().add(jStartScreenPanel);
    }

    private ActionListener getNewGameListener() {
        return (actionEvent -> {
            JPanel inputPanel = new JPanel(new GridLayout(2, 2));

            // Board size
            JLabel boardSizeLabel = new JLabel("Size: ", JLabel.CENTER);
            inputPanel.add(boardSizeLabel);
            JComboBox<String> boardSizeComboBox = new JComboBox<>();
            boardSizeComboBox.addItem("9x9");
            boardSizeComboBox.addItem("13x13");
            boardSizeComboBox.addItem("19x19");
            inputPanel.add(boardSizeComboBox);

            // Komi
            JLabel komiLabel = new JLabel("Komi: ", JLabel.CENTER);
            inputPanel.add(komiLabel);
            JTextField komiSizeTextField = new JTextField();
            inputPanel.add(komiSizeTextField);

            int boardSizeChoose = JOptionPane.showConfirmDialog(this,
                    inputPanel,
                    "New game",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (boardSizeChoose == JOptionPane.OK_OPTION) {
                int boardSize = Integer.parseInt(((String) boardSizeComboBox.getSelectedItem()).split("x")[0]);
                double komi = Double.parseDouble(komiSizeTextField.getText());
                this.board = new Board(boardSize, komi);
                newGame(boardSize, komi);
            }
        });
    }

    private ActionListener getLoadGameListener() {
        return (actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a file containing a game: ");
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    newGame(Board.loadGame(file));
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null,
                            "<html>Can't open file <b>" + exc.getMessage() + "</b></html>",
                            "Can't open file",
                            JOptionPane.ERROR_MESSAGE);
                } catch (UnsupportedFileFormatException exc) {
                    JOptionPane.showMessageDialog(null,
                            "<html>File <b>" + exc.getMessage() + "</b> has invalid format</html>",
                            "Invalid file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private ActionListener getExitGameListener() {
        return (actionEvent -> {
            String[] options = { "Yes", "No" };
            int returnValue = JOptionPane.showOptionDialog(this,
                    "<html>Are you sure you want to exit?<br>All unsaved progress will be lost</html>",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (returnValue == JOptionPane.OK_OPTION)
                System.exit(0);
        });
    }

    void initMenu() {
        jMenuBar = new JMenuBar();

        jMenuGame = new JMenu("Game");
        jMenuHelp = new JMenu("Help");
        jMenuAbout = new JMenu("About");

        // Game menu
        jGameNew = new JMenuItem("New Game");
        jGameNew.setAccelerator(KeyStroke.getKeyStroke("control N"));

        jGameNew.addActionListener(getNewGameListener());

        jGameSave = new JMenuItem("Save Game");
        jGameSave.setAccelerator(KeyStroke.getKeyStroke("control S"));
        jGameSave.addActionListener(actionEvent -> {
            if (this.board == null)
                return;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a directory to save game: ");
            // TODO
            // fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    this.board.saveGame(file);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null,
                            "<html>Problem saving to file: </b>" + file.getName() + "</b></html>",
                            "Saving error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jGameLoad = new JMenuItem("Load Game");
        jGameLoad.setAccelerator(KeyStroke.getKeyStroke("control O"));
        jGameLoad.addActionListener(getLoadGameListener());

        jGameExit = new JMenuItem("Exit");
        jGameExit.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        jGameExit.addActionListener(getExitGameListener());

        jMenuGame.add(jGameNew);
        jMenuGame.addSeparator();
        jMenuGame.add(jGameSave);
        jMenuGame.add(jGameLoad);
        jMenuGame.addSeparator();
        jMenuGame.add(jGameExit);

        // Help menu
        jMenuHelp.setMnemonic(KeyEvent.VK_F1);
        jMenuHelp.addActionListener(actionEvent -> {
            // TODO
        });

        // About menu
        jMenuAbout.addActionListener(actionEvent -> {
            // TODO
        });

        // Getting it all together
        jMenuBar.add(jMenuGame);
        jMenuBar.add(jMenuHelp);
        jMenuBar.add(jMenuAbout);

        // Setting the menu bar
        this.setJMenuBar(jMenuBar);
    }

    void initBoard() {
        jBoard = new JPanel();
        if (board != null) {
            int boardSize = board.getBoardSize();
            jBoard.setLayout(new GridLayout(boardSize, boardSize));
            jIntersections = new JButton[boardSize][boardSize];
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    jIntersections[x][y] = new JButton(Sprite.getIcon(0, x, y, boardSize));
                    jIntersections[x][y].setEnabled(true);
                    jIntersections[x][y].setBorder(BorderFactory.createEmptyBorder());
                    jIntersections[x][y].setContentAreaFilled(false);
                    jIntersections[x][y].setMinimumSize(new Dimension(BOARD_PANEL_DIMENSION / boardSize, BOARD_PANEL_DIMENSION / boardSize));
                    jIntersections[x][y].setPreferredSize(new Dimension(BOARD_PANEL_DIMENSION / boardSize, BOARD_PANEL_DIMENSION / boardSize));
                    jIntersections[x][y].setMaximumSize(new Dimension(BOARD_PANEL_DIMENSION / boardSize, BOARD_PANEL_DIMENSION / boardSize));

                    int finalX = x;
                    int finalY = y;
                    jIntersections[x][y].addActionListener(actionPerformed -> {
                        if (!isGameGoing)
                            return;
                        if (board.makeMove(finalX, finalY)) {
                            updateBoard();
                            updateUtility();
                        }
                    });
                    jBoard.add(jIntersections[x][y], x, y);
                }
            }

            jBoard.setMinimumSize(BOARD_PANEL);
            jBoard.setPreferredSize(BOARD_PANEL);
            jBoard.setMaximumSize(BOARD_PANEL);
            jBoard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            /*GridBagConstraints boardGridBagConstraints = new GridBagConstraints();
            boardGridBagConstraints.gridx = 0;
            boardGridBagConstraints.gridy = 0;
            boardGridBagConstraints.weightx = BOARD_PANEL_DIMENSION / this.getWidth();
            boardGridBagConstraints.weighty = BOARD_PANEL_DIMENSION / this.getHeight();
            boardGridBagConstraints.anchor = GridBagConstraints.WEST;
            boardGridBagConstraints.fill = GridBagConstraints.NONE;*/

            getContentPane().add(jBoard);
        }
    }

    void initUtility() {

        JPanel jControlsPanel = new JPanel();
        BoxLayout utilityLayout = new BoxLayout(jControlsPanel, BoxLayout.Y_AXIS);
        jControlsPanel.setLayout(utilityLayout);
        jControlsPanel.setPreferredSize(new Dimension(CONTROLLER_PANEL_WIDTH, BOARD_PANEL_DIMENSION));

        // TODO - border indicates who is to move
        // Players info and avatars
        JPanel jPlayerInfo = new JPanel(new GridLayout(3, 2));
        JLabel blackPlayerName = new JLabel("Black (HUMAN)", JLabel.CENTER);
        JLabel whitePlayerName = new JLabel("White (HUMAN)", JLabel.CENTER);
        jPlayerInfo.add(blackPlayerName);
        jPlayerInfo.add(whitePlayerName);
        JLabel blackPlayer = new JLabel(new ImageIcon("sprites/black_avatar.png"));
        JLabel whitePlayer = new JLabel(new ImageIcon("sprites/white_avatar.png"));
        jPlayerInfo.add(blackPlayer);
        jPlayerInfo.add(whitePlayer);

        // Captured stones
        blackPlayerCaptures = new JLabel("0", Sprite.p2, JLabel.CENTER);
        whitePlayerCaptures = new JLabel("0", Sprite.p1, JLabel.CENTER);
        jPlayerInfo.add(blackPlayerCaptures);
        jPlayerInfo.add(whitePlayerCaptures);

        jControlsPanel.add(jPlayerInfo);

        JPanel jContols = new JPanel(new FlowLayout());
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(actionEvent -> {
            board.undo();
            updateBoard();
            updateUtility();
        });

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(actionEvent -> {
            board.redo();
            updateBoard();
            updateUtility();
        });

        JButton passButton = new JButton("Pass");
        passButton.addActionListener(actionEvent -> {
            board.makePass();
            updateBoard();
            updateUtility();
        });

        JButton resignButton = new JButton("Resign");
        resignButton.addActionListener(actionEvent -> {
            String[] options = { "Yes", "No" };
            int returnValue = JOptionPane.showOptionDialog(this,
                    "Are you sure?",
                    "Confirm resign",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (returnValue == JOptionPane.OK_OPTION) {
                this.isGameGoing = false;
                // TODO
            }
        });

        jContols.add(undoButton);
        jContols.add(redoButton);
        jContols.add(passButton);
        jContols.add(resignButton);

        jControlsPanel.add(jContols);

        //Console
        //JTextPane jEventConsole = new JTextPane();

        //jControlsPanel.add(jEventConsole);
        jControlsPanel.setBorder(new BevelBorder(0));

        /*GridBagConstraints utilityGridBagConstraints = new GridBagConstraints();
        utilityGridBagConstraints.ipadx = 50;
        utilityGridBagConstraints.gridx = 1;
        utilityGridBagConstraints.gridy = 0;
        utilityGridBagConstraints.fill = GridBagConstraints.BOTH;*/
        //utilityGridBagConstraints.weightx = CONTROLLER_PANEL_WIDTH / this.getWidth();
        //utilityGridBagConstraints.weighty = BOARD_PANEL_DIMENSION / this.getHeight();
        //utilityGridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        jUtility = jControlsPanel;

        getContentPane().add(jUtility);
    }

    void updateBoard() {
        for (int x = 0; x < board.getBoardSize(); x++) {
            for (int y = 0; y < board.getBoardSize(); y++) {
                jIntersections[x][y].setIcon(Sprite.getIcon(board.getIntersection(x, y).getState(), x, y, board.getBoardSize()));
            }
        }
    }

    void updateUtility() {
        blackPlayerCaptures.setText(String.valueOf(board.getBlackCaptures()));
        whitePlayerCaptures.setText(String.valueOf(board.getWhiteCaptures()));
    }

    void newGame(int boardSize, double komi) {
        if (isGameGoing) {
            getContentPane().remove(jBoard);
            getContentPane().remove(jUtility);
        } else {
            getContentPane().remove(jStartScreenPanel);
        }
        this.setLayout(new FlowLayout());
        this.isGameGoing = true;
        initBoard();
        add(Box.createRigidArea(new Dimension(10, 0)));
        initUtility();
        revalidate();
        repaint();


    }

    void newGame(Board board) {
        this.board = board;
        newGame(board.getBoardSize(), board.getKomi());
        updateBoard();
        updateUtility();
    }


}
