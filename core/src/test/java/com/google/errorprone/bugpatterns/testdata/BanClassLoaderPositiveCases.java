/*
 * Copyright 2023 The Error Prone Authors.
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

package com.google.errorprone.bugpatterns.testdata;

import static java.rmi.server.RMIClassLoader.loadClass;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

class BanClassLoaderPositiveCases {
  /** Load a class using URLClassLoader. */
  public static final Class<?> find() throws ClassNotFoundException, MalformedURLException {
    URLClassLoader loader =
        new URLClassLoader(new URL[] {new URL("eval.com")}) {
          @Override
          protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            // BUG: Diagnostic contains: BanClassLoader
            return findClass(name);
          }
        };
    return loader.loadClass("BadClass");
  }

  /** Calling static methods in java.rmi.server.RMIClassLoader. */
  public static final Class<?> loadRMI() throws ClassNotFoundException, MalformedURLException {
    // BUG: Diagnostic contains: BanClassLoader
    return loadClass("evil.com", "BadClass");
  }

  /** Calling methods of nested class. */
  public static final Class<?> methodHandlesDefineClass(byte[] bytes)
      throws IllegalAccessException {
    // BUG: Diagnostic contains: BanClassLoader
    return MethodHandles.lookup().defineClass(bytes);
  }
}
