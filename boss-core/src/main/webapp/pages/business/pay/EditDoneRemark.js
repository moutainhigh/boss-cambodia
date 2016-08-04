/**
 * 修改流水备注
 */

EditDoneRemarkForm = Ext.extend(BaseForm,{
	url:Constant.ROOT_PATH+"/core/x/Pay!editRemark.action",
	busiFeeGrid : null,
	constructor:function(){
		var record = App.getApp().main.infoPanel.getDoneCodePanel().doneCodeGrid.getSelectionModel().getSelected();
		var doneCode = record.get('done_code');
		var oldRemark = record.get('remark');
		EditDoneRemarkForm.superclass.constructor.call(this,{
			labelWidth:100,
			bodyStyle: 'padding-top:10px',
			items:[{
				xtype:'hidden',
				name:'donecode',
				value:doneCode
			},{
				xtype : 'textarea',
				fieldLabel:lmain('doneCode._form.oldRemark'),
				name:'oldRemark',
				value:oldRemark,
				readOnly:true,
				width:300,height:150
			},{
				xtype : 'textarea',
				fieldLabel:lmain('doneCode._form.newRemark'),
				name:'remark',
				width:300,height:150
			}]
		});
	},
	doValid : function(){
		var oldRemark = this.getForm().findField('oldRemark').getValue();
		var remark = this.getForm().findField('remark').getValue();
		if(oldRemark == remark){
			var result = {};
			result["isValid"] = false;
			result["msg"] = "新备注不能和原备注相同";
			return result
		}
	},
	success:function(){
		App.getApp().refreshPanel(App.getApp().getData().currentResource.busicode);
	}
});

Ext.onReady(function(){
	var panel = new EditDoneRemarkForm();
	TemplateFactory.gTemplate(panel);
});