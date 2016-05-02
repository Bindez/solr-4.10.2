package org.apache.lucene.analysis.miscellaneous;

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

import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.smart.mm.*;
import org.junit.Test;

public class TestLengthFilter extends BaseTokenStreamTestCase {
  
  public void testFilterNoPosIncr() throws Exception {
    TokenStream stream = new MockTokenizer(
        new StringReader("short toolong evenmuchlongertext a ab toolong foo"), MockTokenizer.WHITESPACE, false);
    LengthFilter filter = new LengthFilter(Version.LUCENE_4_3, false, stream, 2, 6);
    assertTokenStreamContents(filter,
      new String[]{"short", "ab", "foo"},
      new int[]{1, 1, 1}
    );
  }

  public void testFilterWithPosIncr() throws Exception {
    TokenStream stream = new MockTokenizer(
        new StringReader("short toolong evenmuchlongertext a ab toolong foo"), MockTokenizer.WHITESPACE, false);
    LengthFilter filter = new LengthFilter(stream, 2, 6);
    assertTokenStreamContents(filter,
      new String[]{"short", "ab", "foo"},
      new int[]{1, 4, 2}
    );
  }
  
  public void testEmptyTerm() throws IOException {
    Analyzer a = new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer tokenizer = new KeywordTokenizer(reader);
        return new TokenStreamComponents(tokenizer, new LengthFilter(tokenizer, 0, 5));
      }
    };
    checkOneTerm(a, "", "");
  }

  /**
   * checking the validity of constructor arguments
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArguments() throws Exception {
    new LengthFilter(new MockTokenizer(new StringReader("accept only valid arguments")), -4, -1);
  }
}
