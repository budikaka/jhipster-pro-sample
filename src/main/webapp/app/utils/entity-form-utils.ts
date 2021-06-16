import { ICommonTable } from '@/shared/model/modelConfig/common-table.model';
import { CommonFieldType } from '@/shared/model/enumerations/common-field-type.model';
import { EndUsedType } from '@/shared/model/enumerations/end-used-type.model';
import { RelationshipType } from '@/shared/model/enumerations/relationship-type.model';
import { SourceType } from '@/shared/model/enumerations/source-type.model';
import cloneDeep from 'lodash/cloneDeep';
import { basicsList } from '@/components/jhi-data-form/KFormDesign/config/formItemsConfig';
import pick from 'lodash/pick';
import upperFirst from 'lodash/upperFirst';
import kebabCase from 'lodash/kebabcase';
import moment from 'moment';

function getDefaultItemData(key: string) {
  return cloneDeep(basicsList.find(item => item.type === key));
}

/**
 * 根据实体信息生成表单配置数据
 * @param commonTableData
 */
export function generateDataForDesigner(commonTableData: ICommonTable) {
  const dataContent = [];
  if (commonTableData.commonTableFields) {
    commonTableData.commonTableFields.forEach(field => {
      let validateRulesObject = null;
      let rules = [];
      if (field.validateRules) {
        validateRulesObject = JSON.parse(field.validateRules);
      }
      if (!field.hideInForm) {
        switch (field.type) {
          case CommonFieldType.BOOLEAN: {
            let item = null;
            if (!field.endUsed || field.endUsed.valueOf() === EndUsedType.SWITCH) {
              item = getDefaultItemData('switch');
            } else if (field.endUsed.valueOf() === EndUsedType.RADIO) {
              item = getDefaultItemData('radio');
            } else if (field.endUsed.valueOf() === EndUsedType.CHECKBOX) {
              item = getDefaultItemData('checkbox');
            }
            if (item) {
              if (validateRulesObject) {
                if (validateRulesObject.required) {
                  rules.push({ required: true, message: '必填项' });
                  item.defaultValue = false;
                }
                item.rules = rules;
              }
              item.label = field.title;
              item.model = field.system ? field.entityFieldName : 'extData.' + field.entityFieldName;
              item.key = item.model;
              if (field.clientReadOnly) {
                item.options.disabled = true;
              }
              dataContent.push(item);
            }
            break;
          }
          case CommonFieldType.INTEGER:
          case CommonFieldType.DOUBLE:
          case CommonFieldType.FLOAT:
          case CommonFieldType.LONG: {
            let item = null;
            if (!field.endUsed) {
              item = getDefaultItemData('number');
            } else if (field.endUsed.valueOf() === EndUsedType.RATE) {
              item = getDefaultItemData('rate');
            } else if (field.endUsed.valueOf() === EndUsedType.SLIDER) {
              item = getDefaultItemData('slider');
            }
            if (item) {
              if (validateRulesObject) {
                if (validateRulesObject.required) {
                  rules.push({ required: true, message: '必填项' });
                  item.defaultValue = 0;
                }
                if (validateRulesObject.min) {
                  rules.push({ type: 'number', min: validateRulesObject.min, message: `必须大于${validateRulesObject.min}` });
                }
                if (validateRulesObject.max) {
                  rules.push({ type: 'number', max: validateRulesObject.max, message: `必须小于${validateRulesObject.max}` });
                }
                item.rules = rules;
              }
              item.label = field.title;
              item.model = field.system ? field.entityFieldName : 'extData.' + field.entityFieldName;
              item.key = item.model;
              if (field.clientReadOnly) {
                item.options.disabled = true;
              }
              dataContent.push(item);
            }
            break;
          }
          case CommonFieldType.ZONED_DATE_TIME: {
            const item = getDefaultItemData('date');
            if (validateRulesObject) {
              if (validateRulesObject.required) {
                rules.push({ required: true, message: '必填项' });
                item.defaultValue = moment();
              }
              item.rules = rules;
            }
            item.label = field.title;
            item.model = field.system ? field.entityFieldName : 'extData.' + field.entityFieldName;
            item.key = item.model;
            item.options.format = 'YYYY-MM-DD hh:mm:ss';
            if (field.clientReadOnly) {
              item.options.disabled = true;
            }
            dataContent.push(item);
            break;
          }
          case CommonFieldType.STRING: {
            let item = null;
            if (!field.endUsed) {
              item = getDefaultItemData('input');
              item.options.type = 'text';
              item.options.clearable = true;
            } else if (field.endUsed.valueOf() === EndUsedType.EDITOR) {
              item = getDefaultItemData('editor');
            } else if (field.endUsed.valueOf() === EndUsedType.CODE_EDITOR) {
              item = getDefaultItemData('codemirror');
            } else if (field.endUsed.valueOf() === EndUsedType.AUDIO_URL) {
              item = getDefaultItemData('uploadFile');
            } else if (field.endUsed.valueOf() === EndUsedType.FILE_URL) {
              item = getDefaultItemData('uploadFile');
            } else if (field.endUsed.valueOf() === EndUsedType.IMAGE_URL) {
              item = getDefaultItemData('uploadImg');
            } else if (field.endUsed.valueOf() === EndUsedType.AVATAR) {
              item = getDefaultItemData('uploadImg');
            }
            if (item) {
              if (validateRulesObject) {
                if (validateRulesObject.required) {
                  rules.push({ required: true, message: '必填项' });
                }
                if (validateRulesObject.min) {
                  rules.push({ type: 'string', min: Number(validateRulesObject.min), message: `必须大于${validateRulesObject.min}` });
                }
                if (validateRulesObject.max) {
                  rules.push({ type: 'string', max: Number(validateRulesObject.max), message: `必须小于${validateRulesObject.max}` });
                }
                item.rules = rules;
              }
              item.label = field.title;
              item.model = field.system ? field.entityFieldName : 'extData.' + field.entityFieldName;
              item.key = item.model;
              if (field.clientReadOnly) {
                item.options.disabled = true;
              }
              dataContent.push(item);
            }
            break;
          }
          case CommonFieldType.TEXTBLOB: {
            let item = null;
            if (!field.endUsed || field.endUsed.valueOf() === EndUsedType.EDITOR) {
              item = getDefaultItemData('editor');
            } else if (!field.endUsed || field.endUsed.valueOf() === EndUsedType.CODE_EDITOR) {
              item = getDefaultItemData('codemirror');
            } else if (!field.endUsed || field.endUsed.valueOf() === EndUsedType.TEXT_AREA) {
              item = getDefaultItemData('textarea');
            }
            if (item) {
              if (validateRulesObject) {
                if (validateRulesObject.required) {
                  rules.push({ required: true, message: '必填项' });
                }
                item.rules = rules;
              }
              item.label = field.title;
              item.model = field.system ? field.entityFieldName : 'extData.' + field.entityFieldName;
              item.key = item.model;
              if (field.clientReadOnly) {
                item.options.disabled = true;
              }
              dataContent.push(item);
            }
            break;
          }
          case CommonFieldType.ENUM: {
            let item = null;
            if (!field.endUsed || field.endUsed.valueOf() === EndUsedType.SELECT) {
              item = getDefaultItemData('select');
              item.options.options = [];
              const fieldValuesObject = JSON.parse(field.fieldValues);
              if (fieldValuesObject) {
                Object.keys(fieldValuesObject).forEach(key => {
                  item.options.options.push({ value: key, label: fieldValuesObject[key] });
                });
              }
            } else if (field.endUsed.valueOf() === EndUsedType.RADIO) {
              item = getDefaultItemData('radio');
            }
            if (item) {
              if (validateRulesObject) {
                if (validateRulesObject.required) {
                  rules.push({ required: true, message: '必填项' });
                }
                item.rules = rules;
              }
              item.label = field.title;
              item.model = field.system ? field.entityFieldName : 'extData.' + field.entityFieldName;
              item.key = item.model;
              item.options.dynamic = false;
              item.options.dynamicKey = '';
              if (field.clientReadOnly) {
                item.options.disabled = true;
              }
              dataContent.push(item);
            }
          }
        }
      }
    });
  }
  if (commonTableData.relationships) {
    commonTableData.relationships.forEach(relationship => {
      if (!relationship.hideInForm) {
        switch (relationship.relationshipType) {
          case RelationshipType.MANY_TO_ONE:
            if (relationship.sourceType === SourceType.SERVICE_ENTITY) {
              const item = getDefaultItemData('modalSelect');
              item.options.selectListName = 'jhi-' + kebabCase(relationship.otherEntityName + '-compact');
              item.commonTableName = relationship.otherEntityName;
              item.label = relationship.name;
              item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
              item.model = item.key;
              item.options.valueField = 'id';
              item.options.labelField = relationship.otherEntityField;
              item.options.dynamic = true;
              item.options.dynamicKey = relationship.dataName;
              dataContent.push(item);
            } else if (relationship.sourceType === SourceType.DATA_DICTIONARY) {
              const item = getDefaultItemData('modalSelect');
              item.options.selectListName = 'jhi-' + kebabCase(relationship.otherEntityName + '-compact');
              item.commonTableName = relationship.otherEntityName;
              item.options.parentId = relationship.dataDictionaryNode.id;
              item.label = relationship.name;
              item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
              item.model = item.key;
              item.options.valueField = 'id';
              item.options.labelField = relationship.otherEntityField;
              item.options.dynamic = true;
              item.options.dynamicKey = relationship.dataName;
              dataContent.push(item);
            } else {
              switch (relationship.otherEntityName) {
                case 'UploadFile': {
                  const item = getDefaultItemData('singleUploadFile');
                  item.label = relationship.name;
                  item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
                  item.model = item.key;
                  item.options.fileUrlField = relationship.relationshipName + upperFirst(relationship.otherEntityField);
                  dataContent.push(item);
                  break;
                }
                case 'UploadImage': {
                  const item = getDefaultItemData('singleUploadImg');
                  item.label = relationship.name;
                  item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
                  item.model = item.key;
                  item.options.imageUrlField = relationship.relationshipName + upperFirst(relationship.otherEntityField);
                  dataContent.push(item);
                  break;
                }
                default: {
                  if (relationship.otherEntityIsTree) {
                    const item = getDefaultItemData('treeSelect');
                    item.label = relationship.name;
                    item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
                    item.model = item.key;
                    item.options.multiple = false;
                    item.options.dynamic = true;
                    item.options.dynamicKey = relationship.dataName;
                    item.options.replaceFields.key = 'id';
                    item.options.replaceFields.value = 'id';
                    item.options.replaceFields.title = relationship.otherEntityField;
                    dataContent.push(item);
                  } else {
                    let item = null;
                    if (!relationship.endUsed) {
                      item = getDefaultItemData('modalSelect');
                      item.options.selectListName = 'jhi-' + kebabCase(relationship.otherEntityName + '-compact');
                      item.commonTableName = relationship.otherEntityName;
                    } else if (relationship.endUsed.valueOf() === EndUsedType.SELECT) {
                      item = getDefaultItemData('customSelect');
                    } else if (relationship.endUsed.valueOf() === EndUsedType.MODAL_SELECT) {
                      item = getDefaultItemData('modalSelect');
                      item.options.selectListName = 'jhi-' + kebabCase(relationship.otherEntityName) + '-compact';
                      item.commonTableName = relationship.otherEntityName;
                    }
                    if (item) {
                      item.label = relationship.name;
                      item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
                      item.model = item.key;
                      item.options.valueField = 'id';
                      item.options.labelField = relationship.otherEntityField;
                      item.options.dynamic = true;
                      item.options.dynamicKey = relationship.dataName;
                      dataContent.push(item);
                    }
                  }
                }
              }
            }
            break;
          case RelationshipType.MANY_TO_MANY:
            if (relationship.ownerSide === true) {
              if (relationship.otherEntityIsTree) {
                const item = getDefaultItemData('treeSelect');
                item.label = relationship.name;
                item.model = relationship.relationshipName;
                item.key = relationship.relationshipName;
                item.options.multiple = true;
                item.options.dynamic = true;
                item.options.dynamicKey = relationship.dataName;
                item.options.replaceFields.key = 'id';
                item.options.replaceFields.value = 'id';
                item.options.replaceFields.title = relationship.otherEntityField;
                dataContent.push(item);
              } else {
                const item = getDefaultItemData('customSelect');
                item.label = relationship.name;
                item.model = relationship.relationshipName;
                item.key = relationship.relationshipName;
                item.options.multiple = true;
                item.options.dynamic = true;
                item.options.dynamicKey = relationship.dataName;
                item.options.valueField = 'id';
                item.options.labelField = relationship.otherEntityField;
                dataContent.push(item);
              }
            }
            break;
          case RelationshipType.ONE_TO_ONE:
            if (relationship.ownerSide === true) {
              const item = getDefaultItemData('customSelect');
              item.label = relationship.name;
              item.key = (relationship.system ? '' : 'extData.') + relationship.relationshipName + '.id';
              item.model = item.key;
              item.options.dynamic = true;
              item.options.dynamicKey = relationship.dataName;
              item.options.valueField = 'id';
              item.options.labelField = relationship.otherEntityField;
              dataContent.push(item);
            }
            break;
        }
      }
    });
  }
  return dataContent;
}

export function generateDataFormContent(commonTableData: ICommonTable, relationshipsData, treeNodeId) {
  const dataContent = [
    {
      type: 'number',
      label: 'Id',
      model: 'id',
      key: 'id',
      options: {
        width: '100%',
        placeholder: '系统自动生成',
        clearable: false,
        maxLength: null,
        disabled: true,
      },
    },
  ];
  if (commonTableData.commonTableFields) {
    commonTableData.commonTableFields.forEach(field => {
      if (!field.hideInForm) {
        const item: any = {
          label: field.title,
          model: field.system ? field.entityFieldName : 'extData.' + field.entityFieldName,
          key: field.entityFieldName,
          options: {},
        };
        switch (field.type) {
          case CommonFieldType.BOOLEAN:
            item.type = 'switch';
            break;
          case CommonFieldType.INTEGER:
          case CommonFieldType.DOUBLE:
          case CommonFieldType.FLOAT:
          case CommonFieldType.LONG:
            item.type = 'number';
            break;
          case CommonFieldType.ZONED_DATE_TIME:
            item.type = 'date';
            item.options = {
              width: '100%',
              range: false,
              showTime: true,
              disabled: false,
              clearable: false,
              placeholder: '请选择',
              format: 'YYYY-MM-DD hh:mm:ss',
            };
            break;
          case CommonFieldType.STRING:
            item.type = 'input';
            item.options = {
              type: 'text',
              clearable: true,
            };
            break;
          case CommonFieldType.TEXTBLOB:
            item.type = 'editor';
            item.options = {
              height: 300,
              placeholder: '请输入',
              chinesization: true,
              disabled: false,
              showLabel: false,
              width: '100%',
            };
            break;
        }
        dataContent.push(item);
      }
    });
  }
  if (commonTableData.relationships) {
    commonTableData.relationships.forEach(relationship => {
      if (!relationship.hideInForm) {
        const item: any = { label: relationship.name };
        switch (relationship.relationshipType) {
          case RelationshipType.MANY_TO_ONE:
            item.model = relationship.relationshipName + 'Id';
            item.key = relationship.relationshipName + 'Id';
            switch (relationship.otherEntityName) {
              case 'UploadFile':
                item.type = 'singleUploadFile';
                item.options = {
                  multiple: false,
                  disabled: false,
                  drag: false,
                  width: '100%',
                  limit: 1,
                  listType: 'picture-card',
                  fileUrlField: relationship.relationshipName + upperFirst(relationship.otherEntityField),
                };
                break;
              case 'UploadImage':
                item.type = 'singleUploadImg';
                item.options = {
                  multiple: false,
                  disabled: false,
                  width: '100%',
                  limit: 1,
                  imageUrlField: relationship.relationshipName + upperFirst(relationship.otherEntityField),
                  listType: 'picture-card',
                };
                break;
              default:
                if (relationship.otherEntityIsTree) {
                  item.type = 'treeSelect';
                  item.options = {
                    multiple: false,
                    showSearch: false,
                    treeCheckable: false,
                    dynamic: false,
                    dynamicKey: '',
                    options: toTreeNode(relationshipsData[relationship.dataName], 'id', relationship.otherEntityField, treeNodeId),
                  };
                } else {
                  item.type = 'customSelect';
                  item.options = {
                    options: relationshipsData[relationship.dataName],
                    multiple: false,
                    valueField: 'id',
                    labelField: relationship.otherEntityField,
                  };
                }
            }
            dataContent.push(item);
            break;
          case RelationshipType.MANY_TO_MANY:
            if (relationship.ownerSide === true) {
              item.type = 'customSelect';
              item.model = relationship.relationshipName + 'Id';
              item.key = relationship.relationshipName + 'Id';
              item.options = {
                options: relationshipsData[relationship.dataName],
                multiple: true,
              };
              dataContent.push(item);
            }
            break;
          case RelationshipType.ONE_TO_ONE:
            if (relationship.ownerSide === true) {
              item.type = 'customSelect';
              item.model = relationship.relationshipName + 'Id';
              item.key = relationship.relationshipName + 'Id';
              item.options = {
                options: relationshipsData[relationship.dataName],
                multiple: false,
              };
              dataContent.push(item);
            }
            break;
          case RelationshipType.ONE_TO_MANY:
            if (relationship.otherEntityName === 'UploadFile') {
              item.type = 'uploadFile';
              item.options = {
                multiple: false,
                disabled: false,
                drag: false,
                width: '100%',
                limit: 1,
                listType: 'picture-card',
              };
              if (relationship.options) {
                item.options.limit = JSON.parse(relationship.options).limit;
              }
              item.model = relationship.relationshipName;
              item.key = relationship.relationshipName;
              dataContent.push(item);
            }
            if (relationship.otherEntityName === 'UploadImage') {
              item.type = 'uploadImg';
              item.options = {
                multiple: false,
                disabled: false,
                drag: false,
                width: '100%',
                limit: 1,
                listType: 'picture-card',
              };
              if (relationship.options) {
                item.options.limit = JSON.parse(relationship.options).limit;
              }
              item.model = relationship.relationshipName;
              item.key = relationship.relationshipName;
              dataContent.push(item);
            }
            break;
        }
      }
    });
  }
  return dataContent;
}

function toTreeNode(items: any, valueFieldName: string, labelFieldName: string, currentId?: any, disabledParent: boolean = false) {
  const nzTreeNode = [];
  if (!items) {
    return nzTreeNode;
  }
  items.forEach(item => {
    let disabledChildren = false;
    const option = {
      value: item[valueFieldName],
      label: item[labelFieldName],
      disabled: disabledParent, // 树形关系中自己不能选择自己做为上级对象。
      children: undefined,
    };
    if (item[valueFieldName] === currentId) {
      option.disabled = true;
      disabledChildren = true;
    }
    if (item.children && item.children.length > 0) {
      option.children = toTreeNode(item.children, valueFieldName, labelFieldName, currentId, disabledChildren);
    }
    nzTreeNode.push(option);
  });
  return nzTreeNode;
}

export function getDataByFormField(formFields: any[], data) {
  const fields = formFields.map(field => {
    return field.key;
  });
  return pick(data, fields);
}

export function idsToIdObjectArray(ids: any): any[] {
  const idArray = [];
  const result = [];
  if (typeof ids === 'string') {
    idArray.push(ids.split(','));
  }
  if (Object.prototype.toString.call(ids) === '[object Array]') {
    ids.forEach(id => {
      result.push({ id: id });
    });
  }
  return result;
}

export function idObjectArrayToIdArray(idObjectArray: any[]): any[] {
  const result = [];
  idObjectArray.forEach(idObject => {
    result.push(idObject.id);
  });
  return result;
}
