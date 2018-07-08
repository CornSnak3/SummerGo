package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Sprite {
    public static final int TOKEN_INITIAL_SIZE = 706 / 9;
    private static final ImageIcon background = new ImageIcon(new ImageIcon("sprites/background.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    public static final ImageIcon p1 = new ImageIcon(new ImageIcon("sprites/p1.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    public static final ImageIcon p2 = new ImageIcon(new ImageIcon("sprites/p2.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_c = new ImageIcon(new ImageIcon("sprites/grid_c.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_tl = new ImageIcon(new ImageIcon("sprites/grid_tl.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_tr = new ImageIcon(new ImageIcon("sprites/grid_tr.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_bl = new ImageIcon(new ImageIcon("sprites/grid_bl.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_br = new ImageIcon(new ImageIcon("sprites/grid_br.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_ts = new ImageIcon(new ImageIcon("sprites/grid_ts.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_bs = new ImageIcon(new ImageIcon("sprites/grid_bs.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_rs = new ImageIcon(new ImageIcon("sprites/grid_rs.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid_ls = new ImageIcon(new ImageIcon("sprites/grid_ls.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon illegal = new ImageIcon(new ImageIcon("sprites/illegal.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));

    public static ImageIcon merge(ArrayList<ImageIcon> images) {
        ArrayList<Float> transparency = new ArrayList<>();

        for (ImageIcon i : images) {
            transparency.add(1.0f);
        }

        return merge(images, transparency);
    }

    public static ImageIcon merge(ArrayList<ImageIcon> images, ArrayList<Float> transparency)
    {
        BufferedImage dest = null;
        Graphics2D destG = null;
        int rule; // This is SRC for the top image, and DST_OVER for the other ones
        float alpha;

        for (int i = 0, size = images.size(); i < size; i++)
        {
            Image image = images.get(i).getImage();

            rule = AlphaComposite.SRC_OVER; // Default value
            alpha = transparency.get(i);

            if (i == 0)
            {
                dest = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                destG = dest.createGraphics();

                rule = AlphaComposite.SRC; // Rule for 1st image
            }
            destG.setComposite(AlphaComposite.getInstance(rule, alpha));
            destG.drawImage(image, 0, 0, null);
        }

        return new ImageIcon(dest);
    }

    public static ImageIcon getGridIcon(int x, int y, int boardSize) {
        if (x == 0) {
            if (y == 0)
                return grid_bl;
            else if (y == boardSize - 1)
                return grid_br;
            else
                return grid_bs;
        } else if (x == boardSize - 1) {
            if (y == 0)
                return grid_tl;
            else if (y == boardSize - 1)
                return grid_tr;
            else
                return grid_ts;
        } else {
            if (y == 0)
                return grid_ls;
            else if (y == boardSize - 1)
                return grid_rs;
            else
                return grid_c;
        }
    }

    public static ImageIcon getIcon(int player, int x, int y, int boardSize) {
        if (player == 0)
            return merge(new ArrayList<>(Arrays.asList(background, getGridIcon(x, y, boardSize))));
        return merge(new ArrayList<>(Arrays.asList(background, getGridIcon(x, y, boardSize), (player == -1) ? p1 : p2)));
    }
}
