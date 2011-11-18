package com.google.code.ssm.providers.xmemcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import com.google.code.ssm.config.MemcachedConnectionBean;
import com.google.code.ssm.providers.MemcacheClient;
import com.google.code.ssm.providers.MemcacheClientFactory;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;

/**
 * Copyright (c) 2010, 2011 Jakub Białek
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author Jakub Białek
 * 
 */
public class MemcacheClientFactoryImpl implements MemcacheClientFactory {

    @Override
    public MemcacheClient create(List<InetSocketAddress> addrs, MemcachedConnectionBean connectionBean) throws IOException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(addrs);
        builder.setConnectionPoolSize(1);

        if (connectionBean.isConsistentHashing()) {
            builder.setSessionLocator(new KetamaMemcachedSessionLocator());
        }

        if (connectionBean.isUseBinaryProtocol()) {
            builder.setCommandFactory(new BinaryCommandFactory());
        }

        MemcachedClient client = builder.build();
        client.setOpTimeout(connectionBean.getOperationTimeout());

        if (connectionBean.getMaxAwayTime() != null) {
            client.addStateListener(new ReconnectListener(connectionBean.getMaxAwayTime()));
        }

        return new MemcacheClientWrapper(client);
    }

}