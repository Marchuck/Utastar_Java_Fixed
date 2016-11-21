/*
 Copyright (C) 2013 mc_utastar Development Team

 This file is part of mc_utastar.

 mc_utastar is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 mc_utastar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with RoomEdit. If not, see <http://www.gnu.org/licenses/>.
 */
package gui;

import java.awt.Color;
import java.awt.Graphics;
import uta.dataModel.DataContainer;
import javax.swing.JComponent;

/**
 * Visualize results of the computing
 */
public class DataDiagram extends JComponent {

    public DataDiagram(double[] scoring, DataContainer data) {
        mscoring = scoring;
        mdata = data;
    }

    @Override
    public void paintComponent(Graphics g) {
        int scoreLen = mscoring.length;
        int w = getWidth();
        int h = getHeight();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        if (scoreLen > 0) {
            int margin = 20;
            int bordW = w-2*margin;
            int bordH = h-2*margin;
            
            g.draw3DRect(margin, margin, bordW, bordH, true);
            
            // assume scores in the table are ordered from max to min
            double max = mscoring[0];
            int barH = (int)(0.9 * bordH);
            // display at most 20 elements
            int N = Math.min(scoreLen, 20);
            // divide into N+1 pieces and use N pieces to display bars
            // the N+1 th piece is used for margin barM between bars
            int barW = bordW/(N+1);
            int barM = barW/(N+1);
            
            for(int i = 0; i < N; ++i) {
                int bx = margin + barM + i*(barM+barW);
                int bh = (int) (mscoring[i]/max * barH);
                int by = margin+bordH - bh;
                g.setColor(new Color(239, 74, 74));
                g.fillRect(bx, by, barW, bh);
                g.setColor(Color.BLACK);
                String txtVal = Double.toString(((double)((int)(mscoring[i]*100)))/100);
                g.drawString(txtVal, bx + barW - 15, by + bh - 5);
            }
        } else { // no data
            g.drawLine(0, 0, w, h);
            g.drawLine(0, h, w, 0);
        }

    }
    private double[] mscoring;
    private DataContainer mdata;
}
