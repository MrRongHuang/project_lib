富文本编辑器JS交互事件

question -> editor

JS函数
设置编辑器高度：setEditorHeight(height);
设置占位符：setPlaceholder('placeholder');
设置文本最大长度：setMaxLength(length);
设置HTML内容：setHTMLContent(content);
获取简略文本内容：getBriefContent(); 有返回值(string)
撤销：调用JS函数 undo();
重做：调用JS函数 redo();
H1：调用JS函数 toggleHeadingParagraph(); 重复调用就是切换
加粗：调用JS函数 toggleBold(); 重复调用就是切换
斜体：调用JS函数 toggleItalic(); 重复调用就是切换
下划：调用JS函数toggleUnderline(); 重复调用就是切换
有序：调用JS函数 toggleOrderedList(); 重复调用就是切换
无序：调用JS函数 toggleUnorderedList(); 重复调用就是切换
链接：调用JS函数 insertLink('link', 'linkText');  
插入链接时，如果有选中文本，需要调用JS函数 getSelectedText(); 显示输入弹窗并带入选中的文本，
编辑链接时，调用JS函数 getLinkInfo(); 调用后会产生消息回调，回调里带有A标签的信息，将信息回填到输入弹窗中，确认编辑后调用JS函数 editLink('link', 'linkText'); 
多张图片：调用JS函数 insertImages(['imageURL1', 'imageURL2', 'imageURL3', ...]);
单个视频：调用JS函数 insertVideo('videoURL');
多个视频：调用JS函数 insertVideos(['videoURL1', 'videoURL2', 'videoURL3', ...]);
获取已有图片数量：getImageCount(); 有返回值(int)
获取已有视频数量：getVideoCount(); 有返回值(int)

JS消息回调
contentChanged：变更后的内容(string)
validStateChanged：是否有效的HTML内容 (bool)
textLengthChanged：文本长度变更(int)
briefContent：简要文本内容(string)
undoRedoState：启用状态，包含canUndo(bool是否能撤销) canRedo(bool是否能重做)
h1State:：高亮状态(bool)
boldState：高亮状态(bool)
italicState：高亮状态(bool)
underlineState：高亮状态(bool)
unorderedListState：高亮状态(bool)
orderedListState：高亮状态(bool)
linkState：高亮状态(bool)
editLink: 处理编辑链接包含link(string) linkText(string)

