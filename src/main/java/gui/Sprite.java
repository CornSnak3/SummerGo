package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Sprite {
    public static final int TOKEN_INITIAL_SIZE = 35;
    private static final ImageIcon background = new ImageIcon(new ImageIcon("sprites/background.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon grid = new ImageIcon(new ImageIcon("sprites/grid.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon p1 = new ImageIcon(new ImageIcon("sprites/p1.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));
    private static final ImageIcon p2 = new ImageIcon(new ImageIcon("sprites/p2.png").getImage().getScaledInstance(TOKEN_INITIAL_SIZE, TOKEN_INITIAL_SIZE, Image.SCALE_FAST));

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

    public static ImageIcon getIcon(int player) {
        if (player == 0)
            return grid;
        return merge(new ArrayList<>(Arrays.asList(background, grid, (player == -1) ? p1 : p2)));
    }
}
