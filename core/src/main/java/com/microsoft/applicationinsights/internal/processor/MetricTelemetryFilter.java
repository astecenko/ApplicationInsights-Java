/*
 * ApplicationInsights-Java
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 *
 * MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the ""Software""), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.microsoft.applicationinsights.internal.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.microsoft.applicationinsights.extensibility.TelemetryProcessor;
import com.microsoft.applicationinsights.internal.logger.InternalLogger;
import com.microsoft.applicationinsights.internal.util.LocalStringsUtils;
import com.microsoft.applicationinsights.telemetry.MetricTelemetry;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * The class can filter out MetricTelemetries with configurable names
 *
 * Invalid values would prevent the filter from being used.
 *
 * Created by gupele on 8/7/2016.
 */
public final class MetricTelemetryFilter implements TelemetryProcessor {
    private HashSet<String> notNeeded = new HashSet<String>();

    public void setNotNeeded(String allNotNeeded) throws Throwable{
        try {
            List<String> notNeededAsList = Arrays.asList(allNotNeeded.split(","));
            for (String notNeeded : notNeededAsList) {
                String ready = notNeeded.trim();
                if (LocalStringsUtils.isNullOrEmpty(ready)) {
                    continue;
                }

                this.notNeeded.add(ready);
            }
            InternalLogger.INSTANCE.trace(String.format("MetricTelemetryFilter: set NotNeeded: %s", notNeeded));
        } catch (Throwable t) {
            InternalLogger.INSTANCE.logAlways(InternalLogger.LoggingLevel.ERROR, String.format("MetricTelemetryFilter: failed to parse NotNeededNames: %s", allNotNeeded));
            InternalLogger.INSTANCE.trace("Stack trace generated is %s", ExceptionUtils.getStackTrace(t));
            throw t;
        }
    }

    @Override
    public boolean process(Telemetry telemetry) {
        if (telemetry == null) {
            return true;
        }

        if (telemetry instanceof MetricTelemetry) {
            MetricTelemetry mt = (MetricTelemetry)telemetry;
            if (notNeeded.contains(mt.getName())) {
                return false;
            }
        }
        return true;
    }
}
