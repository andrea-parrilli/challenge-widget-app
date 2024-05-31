package ma.ap.challenge.widgetapp.server.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ap.challenge.widgetapp.server.WidgetAppServerWebConfiguration;
import ma.ap.challenge.widgetapp.server.api.ApiModelAdapter;
import ma.ap.challenge.widgetapp.server.api.dto.WidgetDto;
import me.ap.tools.jackson.deserialize.DeserializerForUpdating;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static com.jayway.jsonpath.internal.JsonFormatter.prettyPrint;
import static ma.ap.challenge.widgetapp.server.ApiPaths.PATH_WIDGET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API and documentation integration tests for the widget controller.
 * <p>
 * This tests Spring HTTP layer and the {@link WidgetController}.
 * Note the context configuration, necessary to pick up the correct {@link ObjectMapper}. By doing so, Spring test ignores
 * the controller in {@code @WebMvcTest}, so it has to be added again in the context.
 */
@WebMvcTest(WidgetController.class)
@ContextConfiguration(classes = {WidgetAppServerWebConfiguration.class, WidgetController.class})
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class WidgetControllerDocumentationTest {
    private final WidgetDto exampleWidget = WidgetDto.builder().id(1L).height(1).width(2).z(3).build();
    private final List<FieldDescriptor> widgetFieldsAll = List.of(
            fieldWithPath("id").description("Autogenerated id for the new Widget. Used everywhere in the API where a specific Widget is involved."),
            fieldWithPath("height").description("The Height of the widget."),
            fieldWithPath("width").description("The width of the Widget."),
            fieldWithPath("z").description("The vertical coordinate of the Widget. Unique among all widgets, changes when another widget is assigned the same 'z'."));
    private final OperationRequestPreprocessor requestPrettyPrint = Preprocessors.preprocessRequest(Preprocessors.prettyPrint());
    private final OperationResponsePreprocessor responsePrettyPrint = Preprocessors.preprocessResponse(Preprocessors.prettyPrint());
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApiModelAdapter model;
    @MockBean
    private DeserializerForUpdating deserializerForUpdating;

    @Test
    void getByIdSucceeds() throws Exception {
        when(model.findById(1L)).thenReturn(Optional.of(exampleWidget));

        mockMvc.perform(get(PATH_WIDGET + "{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "height": 1,
                            "width": 2,
                            "z": 3
                        }"""))
                .andDo(document("widget_get",
                        responsePrettyPrint,
                        responseFields(widgetFieldsAll),
                        pathParameters(parameterWithName("id").description("The id of the desired Widget"))));

        verify(model).findById(1L);
        verifyNoMoreInteractions(model);
    }

    @Test
    void getAll() throws Exception {
        when(model.getAll()).thenReturn(
                List.of(exampleWidget, WidgetDto.builder().id(2L).height(11).width(22).z(33).build()));

        mockMvc.perform(get(PATH_WIDGET))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        [{
                            "id":1,
                            "width":2,
                            "height":1,
                            "z":3},
                        {
                            "id":2,
                            "width":22,
                            "height":11,
                            "z":33}]
                        """))
                .andDo(document("widget_getAll",
                        responsePrettyPrint,
                        responseFields(
                                fieldWithPath("[]").description("The list of all widget known to the system"),
                                fieldWithPath("[].*").description("Widget's fields. See the GET reference for more details."))));
        verify(model).getAll();
        verifyNoMoreInteractions(model);
    }

    @Test
    void createSucceeds() throws Exception {
        when(model.create(any())).thenReturn(exampleWidget);

        mockMvc.perform(post(PATH_WIDGET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "height": 1,
                                    "width": 2,
                                    "z": 3
                                }"""))
                .andDo(document("widget_post",
                        requestPrettyPrint,
                        requestFields(
                                fieldWithPath("height").description("The desired height of the Widget, must be a positive integer."),
                                fieldWithPath("width").description("The desired width of the Widget. Must be a positive integer. "),
                                fieldWithPath("z").description("The desired vertical coordinate of the Widget. Unique among all widgets, changes when another widget is assigned the same 'z'")
                        )))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "height": 1,
                            "width": 2,
                            "z": 3
                        }"""))
                .andDo(document("widget_post", responsePrettyPrint, responseFields(widgetFieldsAll)));

        verify(model).create(exampleWidget.toBuilder().id(null).build());
        verifyNoMoreInteractions(model);
    }

    @Test
    void deleteSucceeds() throws Exception {
        mockMvc.perform(delete(PATH_WIDGET + "{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("widget_delete",
                        pathParameters(parameterWithName("id").description("The id of the Widget to delete. Must be a valid id, but not necessarily existing."))));

        verify(model).delete(1L);
        verifyNoMoreInteractions(model);
    }

    @Test
    void replaceSucceeds() throws Exception {
        when(model.getById(1L)).thenReturn(exampleWidget);
        var updatedWidget = WidgetDto.builder().height(11).width(22).z(33).build();
        when(model.update(exampleWidget, updatedWidget)).thenReturn(updatedWidget.toBuilder().id(1L).build());

        mockMvc.perform(put(PATH_WIDGET + "{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "height": 11,
                                    "width": 22,
                                    "z": 33
                                }"""))
                .andDo(document("widget_put",
                        requestFields(
                                fieldWithPath("height").description("The new height of the Widget, must be a positive integer."),
                                fieldWithPath("width").description("The new width of the Widget. Must be a positive integer. "),
                                fieldWithPath("z").description("The new vertical coordinate of the Widget. Unique among all widgets, changes when another widget is assigned the same 'z'")),
                        pathParameters(parameterWithName("id").description("The id of the Widget to replace. Must exist."))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "height": 11,
                            "width": 22,
                            "z": 33
                        }"""))
                .andDo(document("widget_put", responseFields(widgetFieldsAll)));

        verify(model).getById(1L);
        verify(model).update(exampleWidget, updatedWidget);
        verifyNoMoreInteractions(model);
    }

    @Test
    void updateSucceeds() throws Exception {
        when(model.getById(1L)).thenReturn(exampleWidget);
        var updatedWidget = exampleWidget.toBuilder().height(1).width(22).z(3).build();
        when(model.update(exampleWidget, updatedWidget)).thenReturn(updatedWidget);
        when(deserializerForUpdating.updateFromJson(eq(exampleWidget), any(InputStream.class))).thenReturn(updatedWidget);

        mockMvc.perform(patch(PATH_WIDGET + "{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "width": 22
                                }"""))
                .andDo(document("widget_patch",
                        requestFields(fieldWithPath("*").description("The fields to be updated, excluding the id")),
                        pathParameters(parameterWithName("id").description("The id of the Widget to replace. Must exist."))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "height": 1,
                            "width": 22,
                            "z": 3
                        }"""))
                .andDo(document("widget_patch", responseFields(widgetFieldsAll)));

        verify(model).getById(1L);
        verify(model).update(exampleWidget, updatedWidget);
        verify(deserializerForUpdating).updateFromJson(eq(exampleWidget), any(InputStream.class));
    }
}
