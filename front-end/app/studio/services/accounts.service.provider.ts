/**
 * @license
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Http} from "@angular/http";

import {IAuthenticationService} from "./auth.service";
import {ConfigService} from "./config.service";
import {ILinkedAccountsService} from "./accounts.service";
import {HubLinkedAccountsService} from "./accounts-hub.service";


function LinkedAccountsServiceFactory(http: Http, authService: IAuthenticationService, config: ConfigService): ILinkedAccountsService {
    if (config.apisType() === "hub") {
        console.info("[LinkedAccountsServiceFactory] Creating instance of HubLinkedAccountsService");
        return new HubLinkedAccountsService(http, authService, config);
    } else {
        console.error("[LinkedAccountsServiceFactory] Unknown type for Linked Accounts Service: %s", config.apisType());
        return null;
    }
};


export let LinkedAccountsServiceProvider =
{
    provide: ILinkedAccountsService,
    useFactory: LinkedAccountsServiceFactory,
    deps: [Http, IAuthenticationService, ConfigService]
};

