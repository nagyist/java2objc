/*
 * Copyright (C) 2009 Inderjeet Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.java2objc.objc;

import java.util.HashSet;
import java.util.Set;

/**
 * A Builder to build a user-defined Objective C type 
 * 
 * @author Inderjeet Singh
 */
public final class UserDefinedObjcTypeBuilder {
  private final String name;
  private final Set<ObjcType> baseClasses;
  private final Set<ObjcType> imports;
  private final Set<ObjcMethod> methods;
  private final Set<ObjcField> fields;
  private boolean isInterface;
  
  public UserDefinedObjcTypeBuilder(String name, Set<ObjcType> imports) {
    this.name = name;
    this.imports = imports;
    this.isInterface = false;
    this.baseClasses = new HashSet<ObjcType>();
    this.methods = new HashSet<ObjcMethod>();
    this.fields = new HashSet<ObjcField>();
  }

  public ObjcType build() {
    ObjcType baseClass = baseClasses.isEmpty() ? NSObject.INSTANCE : baseClasses.iterator().next();
    return new ObjcType(name, isInterface, baseClass, imports, methods, fields);
  }
  
  public UserDefinedObjcTypeBuilder setIsInterface(boolean isInterface) {
    this.isInterface = isInterface;
    return this;
  }

  public UserDefinedObjcTypeBuilder addBaseClass(ObjcType baseClass) {
    baseClasses.add(baseClass);
    return this;
  }

  public UserDefinedObjcTypeBuilder addField(ObjcField objcField) {
    fields.add(objcField);
    return this;
  }

  public UserDefinedObjcTypeBuilder addMethod(ObjcMethod objcMethod) {
    methods.add(objcMethod);
    return this;
  }
}