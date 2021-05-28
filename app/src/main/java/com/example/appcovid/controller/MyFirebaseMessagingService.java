/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appcovid.controller;

import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * NOTA: Solo puede haber un servicio en cada aplicación que reciba mensajes FCM. Si es múltiple
 * se declaran en el Manifest, luego se elegirá el primero.
 *
 * Para que esta muestra de Java sea funcional, debe eliminar lo siguiente de la mensajería de Kotlin
 * servicio en el AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */

/**
 * Clase que gestiona las notificaciones provenientes del servidor
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see FirebaseMessagingService
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
}
