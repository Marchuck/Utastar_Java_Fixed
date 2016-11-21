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

package uta;

import core.UtaApp;
import java.util.LinkedList;
import java.util.logging.Level;
import uta.dataModel.*;

/**
 * Implements UTASTAR optimization algorithm.
 */
public class Utastar {

    /**
     * Find most optimal solutions among data.
     *
     * @param data
     * @return Couple of most optimal results
     */
    public static double[] optimize(DataContainer data) {
        LinkedList<DataModel> dt = data.data();
        //temporaly creating table for algorithm
        double[][][] criteria = {{{100, 0}, {2500, 0}, {5000, 0}},
            {{0.1, 0}, {0.55, 0}, {1, 0}},
            {{1, 0},{ 5,0}, {10, 0}},
            {{0.1, 0}, {0.55, 0}, {1, 0}},
            {{-10, 0}, {-5, 0}, {-1, 0}},
            {{0.1, 0}, {0.55, 0}, {1, 0}}};
        int rows = data.rows();
        int cols = data.columns();
        double[][] alternatives = new double[cols - 1][rows];
        // DM preference order
        int[] p = new int[rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                DataParam prm = dt.get(i).params().get(j);
                alternatives[j][i] = prm.val();
                if (prm.optDir().equals(DataParam.OptDirection.MIN)) {
                    alternatives[j][i] = -alternatives[j][i];
                }
            }
            p[i] = (int) dt.get(i).params().getLast().val();
        }

        // run utastar calculations
        MultiTbl MultiTable = new MultiTbl(criteria, alternatives, p);
        UtaSolver ProjectX = new UtaSolver(MultiTable);
        double uted[][] = ProjectX.UtaSolve(0.00001);

        // BEGIN debug info
        int i;
        String dbgStr = "\n averageWeightMatrix :[\n";
        for (i = 0; i < uted.length; i++) {
            for (int t = 0; t < uted[i].length; t++) {
                dbgStr += " " + uted[i][t];
            }
            dbgStr += "\n";
        }
        dbgStr += "]";

        uted = ProjectX.marginalValFuncs();
        dbgStr += "\n\n marginalValueFunctions: [\n";
        for (i = 0; i < uted.length; i++) {
            for (int t = 0; t < uted[i].length; t++) {
                dbgStr += " " + uted[i][t];
            }
            dbgStr += "\n";
        }
        dbgStr += "]";

        double utela[] = ProjectX.altScoring();
        dbgStr += "\n\n alternativeScoring: [";
        for (int t = 0; t < utela.length; t++) {
            dbgStr += " " + utela[t];
        }
        dbgStr += "]";

        UtaApp.mlog.log(Level.INFO, dbgStr);
        // END debug info

        return utela;
    }
}
