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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
       TrieNode root = this;
       for(int i=0; i< s.length(); i++){
           if (!root.children.containsKey(Character.toString(s.charAt(i)))){
               TrieNode newest = new TrieNode();
               newest.isWord=false;
               root.children.put(Character.toString(s.charAt(i)),newest);
           }
           root = root.children.get(Character.toString(s.charAt(i)));
       }
       root.isWord = true;
    }

    public boolean isWord(String s) {
        TrieNode root = this;
      for (int i=0; i< s.length();i++){
          if (!root.children.containsKey(Character.toString(s.charAt(i)))) return false;
          root= root.children.get(Character.toString(s.charAt(i)));
      }
      return (root != null && root.isWord);
    }

    public String getAnyWordStartingWith(String s) {
        String selected = "";
        if (s.length() ==0 || s==null){
            TrieNode root = this;
            while (!root.isWord){
                Object[] letters = root.children.keySet().toArray();
                selected += ((String)letters[0]);
                root = root.children.get(letters[0]);
            }
            //selected+= (String[])root.children.keySet().toArray()[0];
            return selected;
        }
        TrieNode root = this;
        for(int k=0; k< s.length();k++){
            if (root.children.containsKey(Character.toString(s.charAt(k)))){
                root= root.children.get(Character.toString(s.charAt(k)));
                selected += Character.toString(s.charAt(k));
            }
        }
        if (selected.equals(s)){
            while (!root.children.isEmpty()){
              Object[] letters = root.children.keySet().toArray();
                selected += ((String)letters[0]);
                root = root.children.get(letters[0]);
            }
        }
        else return null;
        return selected;
    }

    public String getGoodWordStartingWith(String s) {
        return null;
    }
}
