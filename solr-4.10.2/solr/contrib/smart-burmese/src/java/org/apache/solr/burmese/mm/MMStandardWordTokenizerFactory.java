package org.apache.solr.burmese.mm;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

/**
 * Factory for {@link org.apache.lucene.analysis.ja.JapaneseTokenizer}.
 * <pre class="prettyprint">
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.JapaneseTokenizerFactory"
 *       mode="NORMAL"
 *       userDictionary="user.txt"
 *       userDictionaryEncoding="UTF-8"
 *       discardPunctuation="true"
 *     /&gt;
 *     &lt;filter class="solr.JapaneseBaseFormFilterFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;
 * </pre>
 */
public class MMStandardWordTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {
  private final Mode mode;

  private static final String MODE = "mode";

  
  /** Creates a new JapaneseTokenizerFactory */
  public MMStandardWordTokenizerFactory(Map<String,String> args) {
    super(args);
    mode = Mode.valueOf(get(args, MODE, "SEARCH"));
    
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }
  

  
  
  public void inform(ResourceLoader loader) throws IOException {
  }
  
  public static enum Mode {
    /**
     * Ordinary segmentation: no decomposition for compounds,
     */
    NORMAL, 

    /**
     * Segmentation geared towards search: this includes a 
     * decompounding process for long nouns, also including
     * the full compound token as a synonym.
     */
    SEARCH, 

    /**
     * Extended mode outputs unigrams for unknown words.
     * @lucene.experimental
     */
    EXTENDED
  }

  @Override
  public Tokenizer create(AttributeFactory factory, Reader input) {
    return new MMStandardWordTokenizer(factory,input);
  }
  
 
  
}

