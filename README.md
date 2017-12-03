# File Indexer #

1. 用途：监控指定文件夹并实时生成index.html

2. 配置：配置文件为`config.yml`，包括数据库配置、用户配置（用于访问管理API）等，其中`indexer.directory`节点为要监控的文件夹

3. 配合前级服务器使用，以Nginx反向代理的设置为例：

        server {
            	listen 80;
            	listen [::]:80;
            	
            	server_name files.your.domain;
            	root /var/www/html;
            	
            	location ~ .*/$ {
            		proxy_pass http://localhost:8080;
            		proxy_redirect off;
            	}
        }
    
    上述配置将所有指向目录（即以`/`结尾）的请求转发到file-indexer，其余请求直接指向`/var/www/html`，注意file-indexer将不会处理任何指向文件的请求