/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.length()==0 || prefix == null){
            int index = (int)(Math.random()* words.size());
            return words.get(index);
        }
        else{
            return binarySearch(prefix,0,words.size()-1);
        }
    }
    //helper-method for search:
    private String binarySearch(String tgt, int low, int high){
        if (low > high ) return null;
        int mid = (low+high)/2;
        if (words.get(mid).startsWith(tgt)) return words.get(mid);
        else if (words.get(mid).compareTo(tgt) > 0) return binarySearch(tgt,low,mid-1);
        else return binarySearch(tgt,mid+1,high);
    }
    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;
        if (prefix.length()==0 || prefix == null){
            int index = (int)(Math.random()* words.size());
            selected= words.get(index);
        }
        else{
            //same as getAnyWord:
            selected = binarySearch(prefix,0,words.size()-1);
            //get whole range:
           if (selected != null){
               int tgt_ind = words.indexOf(selected);
               int begin_ind = tgt_ind , end_ind = tgt_ind;
               while (words.get(begin_ind).contains(prefix) && begin_ind>0) begin_ind --;
               while (words.get(end_ind).contains(prefix) && end_ind< words.size()-1) end_ind ++;
               //got range, decide parity of word to increase chances of user completing word:
               for(int i=begin_ind; i< end_ind; i++){
                   //getting the shortest word
                   if (words.get(i).length()- prefix.length() %2 ==0) selected = words.get(i);
               }
           }

        }

        return selected;
    }
}
