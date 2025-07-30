/*******************************************************************************
 * Copyright 2017-2023 Open Text.
 *
 * The only warranties for products and services of Open Text and
 * its affiliates and licensors (“Open Text”) are as may be set forth
 * in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or
 * omissions contained herein. The information contained herein is subject
 * to change without notice.
 *
 * Except as specifically indicated otherwise, this document contains
 * confidential information and a valid license is required for possession,
 * use or copying. If this work is provided to the U.S. Government,
 * consistent with FAR 12.211 and 12.212, Commercial Computer Software,
 * Computer Software Documentation, and Technical Data for Commercial Items are
 * licensed to the U.S. Government under vendor's standard commercial license.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.microfocus.octane.gitlab.testresults;

import com.microfocus.octane.gitlab.helpers.TestResultsHelper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class TestResultsCleanUpRunnable implements Runnable {

    //each 5 minutes delete all old files.
    static public final int INTERVAL = 60;
    static final long TIME = TimeUnit.MINUTES.toMillis(INTERVAL);
    private String testResultsFolderPath="";
    static final Logger log = LogManager.getLogger(TestResultsCleanUpRunnable.class);

    public TestResultsCleanUpRunnable(String folderPath) {
        if (folderPath != null && !folderPath.isEmpty()) {
            testResultsFolderPath = folderPath;
        }
    }

    public void deleteFiles() {
        File folder = TestResultsHelper.getTestResultFolderFullPath(testResultsFolderPath);
        File[] files = folder.listFiles();
        
        if (files == null) {
            log.warn("Unable to list files in directory: {}", folder.getAbsolutePath());
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        for (File fileEntry : files) {
            if (currentTime - fileEntry.lastModified() > TIME) {
                try {
                    FileUtils.deleteDirectory(fileEntry);
                    log.info("Deleted old test results file: {}", fileEntry.getName());
                } catch (IOException e) {
                    log.warn("Unable to delete test results directory: {} - {}", fileEntry.getName(), e.getMessage());
                }
            } else {
                log.debug("Keeping recent file: {}", fileEntry.getName());
            }
        }
    }

    @Override
    public void run() {
        deleteFiles();
    }
}
