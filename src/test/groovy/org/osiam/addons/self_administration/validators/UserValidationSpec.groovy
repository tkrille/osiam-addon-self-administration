/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.addons.self_administration.validators

import org.osiam.addons.self_administration.Config
import org.osiam.addons.self_administration.registration.RegistrationService
import org.osiam.addons.self_administration.registration.RegistrationUser
import org.springframework.context.MessageSource
import spock.lang.Specification

class UserValidationSpec extends Specification {

    RegistrationService registrationService = Mock()

    MessageSource messageSource = Mock()

    Config config = Mock()
    EqualPasswordValidator equalPasswordValidator = new EqualPasswordValidator(config: config)
    PasswordValidator passwordValidator = new PasswordValidator(messageSource: messageSource, config: config)
    PhotoValidator photoValidator = new PhotoValidator()
    EmailValidator registrationEmailValidator = new EmailValidator(registrationService: registrationService, config: config)
    UsernameValidator usernameValidator = new UsernameValidator(registrationService: registrationService, config: config)

    def 'validator should have no errors if all fields are valid'() {
        given:
        RegistrationUser user = new RegistrationUser()
        user.email = 'email@email.de'
        user.photo = '/photo.jpg'
        user.userName = 'userName'
        user.password = 'password'
        user.confirmPassword = 'password'

        config.passwordLength >> 8
        config.usernameEqualsEmail >> true
        registrationService.isUsernameIsAlreadyTaken(_) >> false

        when:
        def isEqualPasswordValid = equalPasswordValidator.isValid(user, null)
        def isPasswordValid = passwordValidator.isValid(user.password, null)
        def isPhotoValid = photoValidator.isValid(user.photo, null)
        def isRegistrationEmailValid = registrationEmailValidator.isValid(user.email, null)
        def isUsernameValid = usernameValidator.isValid(user.userName, null)

        then:
        isEqualPasswordValid == true
        isPasswordValid == true
        isPhotoValid == true
        isRegistrationEmailValid == true
        isUsernameValid == true
    }

    def 'validator should have errors if an empty username was set'() {
        when:
        def isUsernameValid = usernameValidator.isValid('', null)

        then:
        isUsernameValid == false
    }

    def 'validator should have an error if no valid photo URI was set'() {
        when:
        def isPhotoValid = photoValidator.isValid(' ', null)

        then:
        isPhotoValid == false
    }

    def 'validator should have an error if no password was set'() {
        when:
        def isPasswordValid = passwordValidator.isValid(null, null)

        then:
        isPasswordValid == false
    }
}
