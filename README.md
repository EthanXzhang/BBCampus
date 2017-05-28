# BBCampus
帮帮项目团队《帮帮校园》APP in Android. Powered by USTC-LYZZ
帮帮校园APP Android
最后更新时间：2017.5.23 23:11
开发者：Ethan、李瓜瓜

注意事项：
1.机型支持 API23 以上（Android 6.00）
2.Beta版需手动开启APP权限：
	（1）定位权限
	（2）存储权限
	（3）电话及短信权限
	请在手机设置——应用——帮帮校园——权限管理 中开启
	当“定位”及“存储”权限不正确的时候，APP会频繁报“定位错误”
3.请开启手机的GPS功能及4G（3G）功能：
	（1）帮帮校园默认使用高精度定位，优先使用wifi+基站信号定位
	（2）仅使用GPS定位将可能等待过长时间无定位信号
	（3）室内定位精度较差

V1.00已上线功能：
1.帮帮用户注册、登陆：
	（1）使用邮箱号登陆
	（2）手机需填写完整，否则任务接受者无法联系到发起人
2.发布任务与接受任务：
	（1）暂未有用户审核与用户完整信息填写
	（2）任务发布需要后台管理员审核通过，否则APP无法获取任务信息
	（3）Beta版用户注册默认拥有1000帮币与100金额
	（4）任务仅供测试，未有实际现金流水及帮币、积分结算
	（5）进行中与已完成任务将不会再被显示
	（6）进行中任务异常退出后请到用户界面的“我的帮助”中查找（待完善）
3.任务进行：
	（1）现阶段支持“地图”与“详情”两种方式追踪任务
	（2）暂未实现任务实时导航
4.地图定位与导航：
	（1）可以自由设置任务地点
	（2）用户发布时地点将不会随任务上传
	（3）任务支持三种（步行、公交、驾车）导航方式
	（4）暂不支持实时导航跟踪
5.任务获取：
	（1）暂时仅支持本地位置抓取就近任务
	（2）默认最多显示10个最近任务，不限任务类型
	（3）支持“地图”与“列表”两种模式显示待接收任务
	（4）根据类型获取任务功能暂未上线
	（5）任务搜索功能暂未上线
6.广告推送：
	（1）打开“我的订阅”，将可接收到来自服务器推送的自有消息或广告服务
	（2）使用极光SDK实现广告的推送
	（3）暂未上线根据用户“关注”、“技能”、“标签”定制推送

即将上线功能：
1.任务断点复位：
	（1）任务进行中异常退出后，暂时不能自动回到任务
	（2）任务到时关闭交由服务器实现（Beta版本暂未限制任务存在时间）
2.任务的图片上传功能：
	（1）即将上线
	（2）现阶段可选择图片，但无法上传服务器
	（3）带有图片的任务，图片暂为空
3.任务的跟踪导航：
	（1）由于高德导航SDK，现默认使用3D地图导航，部分机型不支持3D地图，暂未上线
	（2）现阶段采用路径规划替代导航
4.用户界面：
	（1）由于产品设计与策划问题，“我的积分”与“我的信用”功能暂未上线
	（2）“订单记录”与“我的任务”界面，跳转任务详情功能即将上线
